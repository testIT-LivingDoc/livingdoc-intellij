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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.domain.*;
import info.novatec.testit.livingdoc.intellij.run.LivingDocConfigurationType;
import info.novatec.testit.livingdoc.intellij.run.RemoteRunConfiguration;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.runner.Main;
import info.novatec.testit.livingdoc.server.domain.Repository;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * LivingDoc execution on selected node (specification).
 *
 * @see AnAction
 * @see info.novatec.testit.livingdoc.intellij.run.LivingDocSettingsEditor
 */
public class ExecuteDocumentAction extends AnAction {

    private final SimpleTree repositoryTree;
    private boolean debugMode = false;

    /**
     * @param tree        {@link SimpleTree} Repository tree.
     * @param isDebugMode Kind of execution: <ul>
     *                    <li>true to activate debug mode</li>
     *                    <li>false otherwise. In this case, you will see the run configuration user interface.</li>
     *                    </ul>
     */
    public ExecuteDocumentAction(final SimpleTree tree, final boolean isDebugMode) {

        super();

        this.repositoryTree = tree;
        this.debugMode = isDebugMode;

        Presentation presentation = getTemplatePresentation();
        if (debugMode) {
            presentation.setText(I18nSupport.getValue("repository.view.action.debug.tooltip"));
            presentation.setDescription(I18nSupport.getValue("repository.view.action.debug.tooltip"));
            presentation.setIcon(AllIcons.Actions.StartDebugger);
        } else {
            presentation.setText(I18nSupport.getValue("repository.view.action.execute.tooltip"));
            presentation.setDescription(I18nSupport.getValue("repository.view.action.execute.tooltip"));
            presentation.setIcon(AllIcons.Actions.Execute);
        }
    }

    /**
     * Action handler. Only specification nodes will be executed.<br>
     * NOTE: Basic functionality with single selection, desired multiple selection. TODO
     *
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent actionEvent) {

        DefaultMutableTreeNode[] nodes = repositoryTree.getSelectedNodes(DefaultMutableTreeNode.class, null);

        Object userObject = nodes[0].getUserObject();

        if (((LDNode) userObject).getType() == LDNodeType.SPECIFICATION) {

            Node node = (Node) userObject;

            LDProject ldProject = new LDProject(actionEvent.getProject());

            RunManager runManager = RunManager.getInstance(ldProject.getIdeaProject());
            LivingDocConfigurationType livingDocConfigurationType = LivingDocConfigurationType.getInstance();

            RunnerAndConfigurationSettings runnerAndConfigurationSettings =
                    runManager.getConfigurationTemplate(livingDocConfigurationType.getConfigurationFactories()[0]);
            runnerAndConfigurationSettings.setName(node.getName());
            runnerAndConfigurationSettings.setTemporary(false);
            runnerAndConfigurationSettings.setActivateToolWindowBeforeRun(true);

            RemoteRunConfiguration runConfiguration =
                    (RemoteRunConfiguration) runnerAndConfigurationSettings.getConfiguration();
            fillRunConfiguration(runConfiguration, node, ldProject);

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

    private void fillRunConfiguration(RemoteRunConfiguration runConfiguration, Node node, final LDProject ldProject) {

        RepositoryNode repositoryNode = RepositoryViewController.getRepositoryNode(node);
        Repository repository = repositoryNode.getRepository();

        runConfiguration.setRepositoryUID(repository.getUid());
        runConfiguration.setRepositoryURL(repository.getBaseTestUrl());
        runConfiguration.setSpecificationName(node.getName());
        runConfiguration.setRepositoryClass(repository.getType().getClassName());
        runConfiguration.setWorkingVersion(node.isUsingCurrentVersion());
        runConfiguration.setUser(ldProject.getUser());
        runConfiguration.setPass(ldProject.getPass());
        runConfiguration.setRepositoryName(repository.getName());

        Module[] modules = ModuleManager.getInstance(ldProject.getIdeaProject()).getModules();
        runConfiguration.setModule(modules[0]);

        runConfiguration.MAIN_CLASS_NAME = Main.class.getName();
    }
}
