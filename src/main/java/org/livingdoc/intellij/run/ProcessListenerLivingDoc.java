package org.livingdoc.intellij.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.BrowserLauncherImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ColorProgressBar;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import info.novatec.testit.livingdoc.report.XmlReport;
import info.novatec.testit.livingdoc.server.domain.Execution;
import info.novatec.testit.livingdoc.server.domain.Specification;
import org.apache.commons.lang3.StringUtils;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.common.PluginProperties;
import org.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * To monitor the execution of a process and capture its output.
 *
 * @see ProcessAdapter
 */
class ProcessListenerLivingDoc extends ProcessAdapter {

    private static final Logger LOG = Logger.getInstance(ProcessListenerLivingDoc.class);

    private final RemoteRunConfiguration runConfiguration;
    private final FilesManager livingDocFilesManager;

    private int totalErrors = 0;
    private int failuresCount = 0;
    private int finishedTestsCount = 0;
    private int ignoreTestsCount = 0;
    private long startTime;
    private long endTime;

    private boolean hasError = false;


    public ProcessListenerLivingDoc(final RemoteRunConfiguration runConfiguration) {

        this.runConfiguration = runConfiguration;
        this.livingDocFilesManager = new FilesManager(this.runConfiguration);
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

                BrowserLauncher browser = new BrowserLauncherImpl();
                browser.open(resultFile.getPath());

            } catch (IOException | SAXException e) {
                LOG.error(e);
            }
        } else {
            runConfiguration.getStatusLine().setText(I18nSupport.getValue("run.execution.error.process"));
            runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.RED);
            runConfiguration.getStatusLine().setFraction(100d);
        }
    }

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

            } else if (ignoreTestsCount >= 1 || failuresCount >= 1 || totalErrors >= 1) {
                runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.YELLOW);

            } else {
                ToolWindow toolWindow = ToolWindowManager.getInstance(runConfiguration.getProject())
                        .getToolWindow(PluginProperties.getValue("toolwindows.id"));
                toolWindow.activate(null);
            }
            runConfiguration.getStatusLine().formatTestMessage(finishedTestsCount + totalErrors + failuresCount + ignoreTestsCount,
                    finishedTestsCount, failuresCount, ignoreTestsCount, endTime - startTime, endTime);
            runConfiguration.getStatusLine().setFraction(1d);

            runConfiguration.getSelectedNode().setIcon(RepositoryViewUtils.getResultIcon(hasError, runConfiguration.getSelectedNode()));
        });
    }

    // FIXME Move LivingDoc dependencies to the connector layer.
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

    private File loadResultFile(final Specification specification) throws IOException {

        File resultFile = livingDocFilesManager.createResultFile();

        Execution execution = specification.getExecutions().iterator().next();
        String content = execution.hasException() ? execution.getExecutionErrorId() : execution.getResults();
        if (StringUtils.isEmpty(content)) {
            content = I18nSupport.getValue("run.execution.error.no.response");
        }
        try (Writer fileWriter = new OutputStreamWriter(new FileOutputStream(resultFile), StandardCharsets.UTF_8)) {
            fileWriter.write(content);
        }

        return resultFile;
    }
}
