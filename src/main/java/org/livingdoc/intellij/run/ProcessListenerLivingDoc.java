package org.livingdoc.intellij.run;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.testframework.ui.TestStatusLine;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.BrowserLauncherImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.util.ColorProgressBar;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang3.StringUtils;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.common.PluginProperties;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.ExecutionCounter;
import org.livingdoc.intellij.domain.LivingDocException;
import org.livingdoc.intellij.domain.LivingDocExecution;
import org.livingdoc.intellij.domain.ProjectSettings;
import org.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;


/**
 * This class will monitor the execution of a LivingDoc execution and it will capture its output.
 *
 * @see ProcessAdapter
 */
public class ProcessListenerLivingDoc extends ProcessAdapter {

    private static final Logger LOG = Logger.getInstance(ProcessListenerLivingDoc.class);

    private final RemoteRunConfiguration runConfiguration;
    private final FilesManager livingDocFilesManager;

    private final ExecutionCounter executionCounter;
    private final TestStatusLine statusLine;

    private boolean hasError = false;


    public ProcessListenerLivingDoc(final RemoteRunConfiguration runConfiguration) {

        this.runConfiguration = runConfiguration;
        this.livingDocFilesManager = new FilesManager(this.runConfiguration);

        this.statusLine = runConfiguration.getStatusLine();
        this.executionCounter = runConfiguration.getExecutionCounter();
    }

    @Override
    public void startNotified(ProcessEvent event) {

        if (executionCounter.getStartTime() == 0) {
            // Set the start time only the first time.
            executionCounter.setStartTime(System.currentTimeMillis());
        }
        SwingUtilities.invokeLater(() -> {
            statusLine.setText(I18nSupport.getValue("run.execution.running.label"));
            statusLine.setStatusColor(ColorProgressBar.GREEN);
            statusLine.setFraction(0d);
        });
    }

    @Override
    public void processTerminated(ProcessEvent processEvent) {

        executionCounter.setEndTime(System.currentTimeMillis());

        if (processEvent.getExitCode() == 0) {
            try {
                LivingDocExecution execution = getLivingDocExecution();

                updateStatusLine(execution);

                File resultFile = loadResultFile(execution);

                BrowserLauncher browser = new BrowserLauncherImpl();
                browser.open(resultFile.getPath());

            } catch (IOException | LivingDocException e) {
                LOG.error(e);
            }
        } else {
            statusLine.setText(I18nSupport.getValue("run.execution.error.process"));
            statusLine.setStatusColor(ColorProgressBar.RED);
            statusLine.setFraction(100d);
        }
    }

    private void updateStatusLine(final LivingDocExecution execution) {

        if (!hasError && (execution.hasException() || execution.hasFailed())) {
            hasError = true;
        }

        executionCounter.setTotalErrors(executionCounter.getTotalErrors() + execution.getErrors());
        executionCounter.setFailuresCount(executionCounter.getFailuresCount() + execution.getFailures());
        executionCounter.setFinishedTestsCount(executionCounter.getFinishedTestsCount() + execution.getSuccess());
        executionCounter.setIgnoreTestsCount(executionCounter.getIgnoreTestsCount() + execution.getIgnored());

        SwingUtilities.invokeLater(() -> {

            if (hasError) {
                runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.RED);

            } else if (executionCounter.getIgnoreTestsCount() >= 1 || executionCounter.getFailuresCount() >= 1 || executionCounter.getTotalErrors() >= 1) {
                runConfiguration.getStatusLine().setStatusColor(ColorProgressBar.YELLOW);

            } else {
                ToolWindow toolWindow = ToolWindowManager.getInstance(runConfiguration.getProject())
                        .getToolWindow(PluginProperties.getValue("toolwindows.id"));
                toolWindow.activate(null);
            }

            int testsTotal = executionCounter.getFinishedTestsCount() + executionCounter.getTotalErrors()
                    + executionCounter.getFailuresCount() + executionCounter.getIgnoreTestsCount();

            Long duration = executionCounter.getEndTime() - executionCounter.getStartTime();

            statusLine.formatTestMessage(
                    testsTotal,
                    executionCounter.getFinishedTestsCount(),
                    executionCounter.getFailuresCount(),
                    executionCounter.getIgnoreTestsCount(),
                    duration,
                    executionCounter.getEndTime());

            statusLine.setFraction(1d);

            runConfiguration.getSelectedNode().setIcon(RepositoryViewUtils.getResultIcon(hasError, runConfiguration.getSelectedNode()));
        });
    }

    private LivingDocExecution getLivingDocExecution() throws IOException, LivingDocException {

        File reportFile = livingDocFilesManager.createReportFile();

        LivingDocConnector livingDocConnector = LivingDocConnector.newInstance(ProjectSettings.getInstance(runConfiguration.getProject()));
        return livingDocConnector.getSpecificationExecution(runConfiguration, reportFile);
    }

    private File loadResultFile(final LivingDocExecution execution) throws IOException {

        File resultFile = livingDocFilesManager.createResultFile();

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
