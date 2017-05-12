package org.livingdoc.intellij.gui.toolwindows.action;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.core.ConfigurationTypeLivingDoc;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;
import org.livingdoc.intellij.gui.toolwindows.ToolWindowPanel;
import org.livingdoc.intellij.run.RemoteRunConfiguration;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * LivingDoc execution on selected node (specification).
 * See {@link #update(AnActionEvent)} for the display restrictions.
 *
 * @see AnAction
 * @see RemoteRunConfiguration
 */
public class ExecuteSpecificationAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(ExecuteSpecificationAction.class);

    @NotNull
    private final ToolWindowPanel toolWindowPanel;
    private boolean debugMode = false;

    /**
     * Creates the action with its text, description and icon.
     *
     * @param toolWindowPanel {@link ToolWindowPanel} User interface for the Repository View.
     * @param isDebugMode     Kind of execution: <ul>
     *                        <li>true to activate debug mode</li>
     *                        <li>false otherwise. In this case, you will see the run configuration user interface.</li></ul>
     */
    public ExecuteSpecificationAction(final ToolWindowPanel toolWindowPanel, final boolean isDebugMode) {

        super();

        this.toolWindowPanel = toolWindowPanel;
        this.debugMode = isDebugMode;

        String text;
        Icon icon;

        if (debugMode) {
            text = I18nSupport.getValue("toolwindows.action.debug.tooltip");
            icon = AllIcons.Actions.StartDebugger;

        } else {
            text = I18nSupport.getValue("toolwindows.action.execute.tooltip");
            icon = AllIcons.Actions.Execute;
        }

        Presentation presentation = getTemplatePresentation();
        presentation.setText(text);
        presentation.setDescription(text);
        presentation.setIcon(icon);
    }


    /**
     * Action handler. Only specification nodes will be executed.<br>
     *
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent actionEvent) {

        toolWindowPanel.resetExecutionCounter();

        DefaultMutableTreeNode[] nodes = toolWindowPanel.getRepositoryTree().getSelectedNodes(DefaultMutableTreeNode.class, null);
        Project project = actionEvent.getProject();
        assert project != null;

        RunManager runManager = RunManager.getInstance(project);
        ConfigurationTypeLivingDoc livingDocConfigurationType = ConfigurationTypeLivingDoc.getInstance();

        for (DefaultMutableTreeNode selectedNode : nodes) {

            Object userObject = selectedNode.getUserObject();

            if (userObject instanceof SpecificationNode) {

                SpecificationNode specificationNode = (SpecificationNode) userObject;

                RunnerAndConfigurationSettings runnerAndConfigurationSettings = runManager.createRunConfiguration(project.getName(), livingDocConfigurationType.getConfigurationFactories()[0]);


                fillRunnerAndConfigurationSettings(runnerAndConfigurationSettings, debugMode, specificationNode);

                RemoteRunConfiguration runConfiguration =
                        (RemoteRunConfiguration) runnerAndConfigurationSettings.getConfiguration();

                fillRunConfigurationForSpecificationNode(runConfiguration, specificationNode);

                Executor executor = debugMode ? DefaultDebugExecutor.getDebugExecutorInstance() : DefaultRunExecutor.getRunExecutorInstance();

                runSpecification(executor, runnerAndConfigurationSettings);
            }
        }
    }

    private void runSpecification(Executor executor, RunnerAndConfigurationSettings runnerAndConfigurationSettings) {
        try {
            ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(executor, runnerAndConfigurationSettings);
            ExecutionEnvironment environment = builder.build();
            environment.getRunner().execute(environment);
        } catch (ExecutionException e) {
            LOG.error(e);
        }
    }

    /**
     * This action will be enabled only for executable nodes
     *
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void update(AnActionEvent actionEvent) {

        super.update(actionEvent);

        DefaultMutableTreeNode[] selectedNodes = toolWindowPanel.getRepositoryTree().getSelectedNodes(DefaultMutableTreeNode.class, null);

        RepositoryViewUtils.setEnabledForExecutableNode(selectedNodes, actionEvent.getPresentation());
    }

    private void fillRunConfigurationForSpecificationNode(@NotNull final RemoteRunConfiguration runConfiguration, final SpecificationNode specificationNode) {

        ModuleNode moduleNode = RepositoryViewUtils.getModuleNode(specificationNode);
        runConfiguration.getAllModules().stream().filter(
                module -> StringUtils.equals(module.getName(), moduleNode.getModuleName())).forEach(runConfiguration::setModule);

        RepositoryNode repositoryNode = RepositoryViewUtils.getRepositoryNode(specificationNode);

        runConfiguration.setRepositoryUID(repositoryNode.getUid());
        runConfiguration.setRepositoryURL(repositoryNode.getBaseTestUrl());
        runConfiguration.setSpecificationName(specificationNode.getNodeName());
        runConfiguration.setRepositoryClass(repositoryNode.getTypeClassName());
        runConfiguration.setCurrentVersion(specificationNode.isUsingCurrentVersion());
        runConfiguration.setRepositoryName(repositoryNode.getName());

        LivingDocConnector livingDocConnector = LivingDocConnector.newInstance(ProjectSettings.getInstance(runConfiguration.getProject()));
        runConfiguration.MAIN_CLASS_NAME = livingDocConnector.getLivingDocMainClass();

        runConfiguration.setStatusLine(toolWindowPanel.getStatusLine());
        runConfiguration.setExecutionCounter(toolWindowPanel.getExecutionCounter());
        runConfiguration.setSelectedNode(specificationNode);

        runConfiguration.setShowConsoleOnStdOut(true);
        runConfiguration.setShowConsoleOnStdErr(true);

        ModuleSettings moduleSettings = ModuleSettings.getInstance(runConfiguration.getConfigurationModule().getModule());
        String programParameter = "";
        if (StringUtils.isNotBlank(moduleSettings.getSudClassName())) {
            programParameter = "-f " + moduleSettings.getSudClassName();

            if (StringUtils.isNotBlank(moduleSettings.getSudArgs())) {
                programParameter = programParameter + ";" + moduleSettings.getSudArgs();
            }
        }
        runConfiguration.setProgramParameters(programParameter);
    }

    private void fillRunnerAndConfigurationSettings(@NotNull final RunnerAndConfigurationSettings runnerAndConfigurationSettings,
                                                    final boolean debugMode, @NotNull final SpecificationNode specificationNode) {

        runnerAndConfigurationSettings.setTemporary(false);

        // True to active the "Run" ToolWindow
        runnerAndConfigurationSettings.setActivateToolWindowBeforeRun(debugMode);

        // True to show the "run configuration UI" before launching LivingDoc
        runnerAndConfigurationSettings.setEditBeforeRun(debugMode);
        runnerAndConfigurationSettings.setName(specificationNode.getNodeName());
    }
}
