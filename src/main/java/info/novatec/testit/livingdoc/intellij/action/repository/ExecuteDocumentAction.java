package info.novatec.testit.livingdoc.intellij.action.repository;

import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import info.novatec.testit.livingdoc.intellij.domain.*;
import info.novatec.testit.livingdoc.intellij.run.LivingDocConfigurationType;
import info.novatec.testit.livingdoc.intellij.run.RemoteRunConfiguration;
import info.novatec.testit.livingdoc.intellij.ui.RepositoryViewUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.runner.Main;
import info.novatec.testit.livingdoc.server.domain.Repository;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * LivingDoc execution on selected node (specification).
 * See {@link #update(AnActionEvent)} for the display restrictions.
 *
 * @see AnAction
 * @see RemoteRunConfiguration
 */
public class ExecuteDocumentAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(ExecuteDocumentAction.class);

    private final RepositoryViewUI repositoryViewUI;
    private boolean debugMode = false;

    /**
     * Creates the action with its text, description and icon.
     *
     * @param repositoryViewUI {@link RepositoryViewUI} User interface fot Repository View.
     * @param isDebugMode      Kind of execution: <ul>
     *                         <li>true to activate debug mode</li>
     *                         <li>false otherwise. In this case, you will see the run configuration user interface.</li></ul>
     */
    public ExecuteDocumentAction(final RepositoryViewUI repositoryViewUI, final boolean isDebugMode) {

        super();

        this.repositoryViewUI = repositoryViewUI;
        this.debugMode = isDebugMode;

        String text;
        Icon icon;

        if (debugMode) {
            text = I18nSupport.getValue("repository.view.action.debug.tooltip");
            icon = AllIcons.Actions.StartDebugger;

        } else {
            text = I18nSupport.getValue("repository.view.action.execute.tooltip");
            icon = AllIcons.Actions.Execute;
        }

        Presentation presentation = getTemplatePresentation();
        presentation.setText(text);
        presentation.setDescription(text);
        presentation.setIcon(icon);
    }

    /**
     * Action handler. Only specification nodes will be executed.<br>
     * TODO NOTE: Basic functionality with single selection, desired multiple selection.
     *
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent actionEvent) {

        DefaultMutableTreeNode[] nodes = repositoryViewUI.getRepositoryTree().getSelectedNodes(DefaultMutableTreeNode.class, null);

        Object userObject = nodes[0].getUserObject();

        if (((LDNode) userObject).getType() == LDNodeType.SPECIFICATION) {

            SpecificationNode specificationNode = (SpecificationNode) userObject;

            LDProject ldProject = new LDProject(actionEvent.getProject());

            RunManager runManager = RunManager.getInstance(ldProject.getIdeaProject());
            LivingDocConfigurationType livingDocConfigurationType = LivingDocConfigurationType.getInstance();

            RunnerAndConfigurationSettings runnerAndConfigurationSettings =
                    runManager.getConfigurationTemplate(livingDocConfigurationType.getConfigurationFactories()[0]);
            runnerAndConfigurationSettings.setName(specificationNode.getName());
            runnerAndConfigurationSettings.setTemporary(false);

            // True to active the "Run" ToolWindow
            runnerAndConfigurationSettings.setActivateToolWindowBeforeRun(false);

            // True to show the "run configuration UI" before launching LivingDoc
            runnerAndConfigurationSettings.setEditBeforeRun(true);

            RemoteRunConfiguration runConfiguration =
                    (RemoteRunConfiguration) runnerAndConfigurationSettings.getConfiguration();
            fillRunConfiguration(runConfiguration, specificationNode, ldProject);

            Executor executor;
            if (debugMode) {
                runnerAndConfigurationSettings.setEditBeforeRun(true);
                executor = DefaultDebugExecutor.getDebugExecutorInstance();
            } else {
                executor = DefaultRunExecutor.getRunExecutorInstance();
            }

            ProgramRunnerUtil.executeConfiguration(ldProject.getIdeaProject(), runnerAndConfigurationSettings, executor);
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

        DefaultMutableTreeNode[] selectedNodes = repositoryViewUI.getRepositoryTree().getSelectedNodes(DefaultMutableTreeNode.class, null);

        RepositoryViewUtils.setEnabledForExecutableNode(selectedNodes, actionEvent.getPresentation());
    }

    private void fillRunConfiguration(RemoteRunConfiguration runConfiguration, final SpecificationNode specificationNode, final LDProject ldProject) {

        RepositoryNode repositoryNode = RepositoryViewController.getRepositoryNode(specificationNode);
        Repository repository = repositoryNode.getRepository();

        runConfiguration.setRepositoryUID(repository.getUid());
        runConfiguration.setRepositoryURL(repository.getBaseTestUrl());
        runConfiguration.setSpecificationName(specificationNode.getName());
        runConfiguration.setRepositoryClass(repository.getType().getClassName());
        runConfiguration.setCurrentVersion(specificationNode.isUsingCurrentVersion());
        runConfiguration.setUser(ldProject.getUser());
        runConfiguration.setPass(ldProject.getPass());
        runConfiguration.setRepositoryName(repository.getName());

        // TODO INIT Review ********************************************************************************************
        // Modify this coden when the project configuration is i the project structure window.
        Module[] modules = ModuleManager.getInstance(ldProject.getIdeaProject()).getModules();
        runConfiguration.setModule(modules[0]);
        // TODO END Review *********************************************************************************************

        runConfiguration.MAIN_CLASS_NAME = Main.class.getName();

        runConfiguration.setStatusLine(repositoryViewUI.getStatusLine());
    }
}
