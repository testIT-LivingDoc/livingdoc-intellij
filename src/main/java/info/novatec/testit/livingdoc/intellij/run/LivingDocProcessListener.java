package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.BrowserLauncherImpl;
import com.intellij.ide.browsers.WebBrowserManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ColorProgressBar;
import com.intellij.openapi.vfs.VirtualFile;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import info.novatec.testit.livingdoc.report.XmlReport;
import info.novatec.testit.livingdoc.server.domain.Execution;
import info.novatec.testit.livingdoc.server.domain.Specification;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.*;


/**
 * To monitor the execution of a process and capture its output.
 *
 * @see ProcessAdapter
 */
public class LivingDocProcessListener extends ProcessAdapter {

    private static final Logger LOG = Logger.getInstance("#info.novatec.testit.livingdoc.intellij.run.LivingDocProcessListener");

    private final RemoteRunConfiguration runConfiguration;
    private final LivingDocFilesManager livingDocFilesManager;

    private int totalErrors = 0;
    private int failuresCount = 0;
    private int finishedTestsCount = 0;
    private int ignoreTestsCount = 0;
    private long startTime;
    private long endTime;

    private boolean hasError = false;


    public LivingDocProcessListener(final RemoteRunConfiguration runConfiguration) {

        this.runConfiguration = runConfiguration;

        this.livingDocFilesManager = new LivingDocFilesManager(runConfiguration.getRepositoryUID(),
                runConfiguration.getSpecificationName());
    }

    @Override
    public void startNotified(ProcessEvent event) {

        startTime = System.currentTimeMillis();

        SwingUtilities.invokeLater(() -> {

            runConfiguration.getStatusLine().setText(I18nSupport.getValue("run.execution.running.label"));
            runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.GREEN);
            runConfiguration.getStatusLine().setFraction(0d);
        });
    }

    @Override
    public void processTerminated(ProcessEvent processEvent) {

        endTime = System.currentTimeMillis();

        if (processEvent.getExitCode() == 0) {

            try {
                Specification specification = buildSpecificationReport();
                updateStatusLine(specification);

                File resultFile = loadResultFile(specification);

                createOrUpdateIntellijResource(resultFile);

            } catch (IOException | SAXException e) {
                LOG.error(e);
            }
        }
    }

    // TODO Set the node icon depending on the execution result. Use LivingDoc Icon like in the eclipse plugin.
    private void updateStatusLine(Specification specification) {

        for (Execution execution : specification.getExecutions()) {
            if (!hasError && (execution.hasException() || execution.hasFailed())) {
                hasError = true;
            }

            totalErrors = totalErrors + execution.getErrors();
            failuresCount = failuresCount + execution.getFailures();
            finishedTestsCount = finishedTestsCount + execution.getSuccess();
            ignoreTestsCount = ignoreTestsCount + execution.getIgnored();
        }

        SwingUtilities.invokeLater(() -> {
            if (hasError) {
                runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.RED);
            }

            runConfiguration.getStatusLine().formatTestMessage(finishedTestsCount + totalErrors + failuresCount,
                    finishedTestsCount, failuresCount, ignoreTestsCount, endTime - startTime, endTime);
            runConfiguration.getStatusLine().setFraction(1d);
        });

    }

    private Specification buildSpecificationReport() throws IOException, SAXException {

        File reportFile = livingDocFilesManager.createReportFile();
        XmlReport xmlReport = XmlReport.parse(reportFile);

        Specification specification = Specification.newInstance(runConfiguration.getSpecificationName());
        specification.setRepository(runConfiguration.getRepository());

        Execution execution = Execution.newInstance(specification, null, xmlReport);
        execution.setResults(xmlReport.getResults(0));

        specification.addExecution(execution);

        return specification;
    }

    private void createOrUpdateIntellijResource(@NotNull final File resultFile) {

        Application application = ApplicationManager.getApplication();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                VirtualFile projectDir = runConfiguration.getProject().getBaseDir();
                String folderName = PluginProperties.getValue("livingdoc.dir.project");
                try {
                    VirtualFile ldDir = projectDir.findChild(folderName);
                    if (ldDir == null) {
                        ldDir = projectDir.createChildDirectory(this, folderName);
                    }

                    VirtualFile ldFile = ldDir.findChild(resultFile.getName());
                    if (ldFile == null) {
                        ldFile = ldDir.createChildData(this, resultFile.getName());
                    }

                    InputStream inputStream = new FileInputStream(resultFile);
                    ldFile.setBinaryContent(IOUtils.toByteArray(inputStream));
                    inputStream.close();

                    BrowserLauncher browser = new BrowserLauncherImpl();
                    browser.browse(ldFile.getUrl(), WebBrowserManager.getInstance().getFirstActiveBrowser(),
                            runConfiguration.getProject());

                } catch (IOException ioe) {
                    LOG.error(ioe.getMessage());
                }
            }
        };

        if (application.isDispatchThread()) {
            application.runWriteAction(runnable);

        } else {
            application.invokeLater(() -> application.runWriteAction(runnable));
        }
    }

    private File loadResultFile(final Specification specification) throws IOException {

        File resultFile = livingDocFilesManager.createResultFile();

        Execution execution = specification.getExecutions().iterator().next();
        String content = execution.hasException() ? execution.getExecutionErrorId() : execution.getResults();
        if (StringUtils.isEmpty(content)) {
            content = I18nSupport.getValue("run.execution.error.noresponse");
        }
        try (FileWriter fileWriter = new FileWriter(resultFile)) {
            fileWriter.write(content);
        }

        return resultFile;
    }
}
