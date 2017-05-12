package org.livingdoc.intellij.gui.toolwindows;

import com.intellij.execution.testframework.ui.TestStatusLine;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.util.ColorProgressBar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import org.apache.commons.lang3.StringUtils;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.common.NodeType;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.gui.toolwindows.action.ExecuteSpecificationAction;
import org.livingdoc.intellij.gui.toolwindows.action.OpenRemoteDocumentAction;
import org.livingdoc.intellij.gui.toolwindows.action.SwitchVersionAction;
import org.livingdoc.intellij.gui.toolwindows.action.TagImplementedAction;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.Collection;


/**
 * User interface for LivingDoc Repository View.<br>
 *
 * @see SimpleToolWindowPanel
 */
public class ToolWindowPanel extends SimpleToolWindowPanel {

    private static final Logger LOG = Logger.getInstance(ToolWindowPanel.class);

    private final transient Project project;

    private final JBPanel mainContent;
    private final DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private transient ActionToolbar toolBar;
    private transient DefaultActionGroup actionGroup;
    private SimpleTree tree;
    private TestStatusLine statusLine;
    private ExecutionCounter executionCounter;
    private transient AnAction refreshAction;


    public ToolWindowPanel(Project project) {
        super(false);

        this.project = project;

        mainContent = new JBPanel(new BorderLayout());
        mainContent.setAutoscrolls(true);
        setContent(mainContent);

        this.rootNode = new DefaultMutableTreeNode(getDefaultRootNode());

        createRepositoryTree();
        createActionToolBar();
        createStatusLine();
        createExecutionCounter();

        configureActions();

        loadRepositories();
    }

    public SimpleTree getRepositoryTree() {
        return this.tree;
    }

    public TestStatusLine getStatusLine() {
        return this.statusLine;
    }

    public ExecutionCounter getExecutionCounter() {
        return this.executionCounter;
    }

    public void resetExecutionCounter() {
        executionCounter.setTotalErrors(0);
        executionCounter.setFailuresCount(0);
        executionCounter.setFinishedTestsCount(0);
        executionCounter.setIgnoreTestsCount(0);
        executionCounter.setStartTime(0);
        executionCounter.setEndTime(0);
    }

    public AnAction getRefreshAction() {
        return this.refreshAction;
    }

    private void createActionToolBar() {

        ActionManager actionManager = ActionManager.getInstance();
        actionGroup = new DefaultActionGroup(null, true);

        toolBar = actionManager.createActionToolbar("LivingDoc.RepositoryViewToolbar", actionGroup, false);
        toolBar.adjustTheSameSize(true);
        toolBar.setTargetComponent(tree);
        setToolbar(toolBar.getComponent());
    }

    private void createRepositoryTree() {

        tree = new SimpleTree();
        tree.setCellRenderer(new TreeCellRendererLivingDoc());
        tree.setRootVisible(true);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        treeModel = new DefaultTreeModel(rootNode, true);
        tree.setModel(treeModel);

        JBScrollPane scrollPane = new JBScrollPane(tree);
        mainContent.add(scrollPane, BorderLayout.CENTER);
    }

    private void createStatusLine() {

        statusLine = new TestStatusLine();
        statusLine.setPreferredSize(false);
        resetStatusLine();
        mainContent.add(statusLine, BorderLayout.NORTH);
    }

    private void resetStatusLine() {
        statusLine.setText("");
        statusLine.setStatusColor(ColorProgressBar.GREEN);
        statusLine.setFraction(0d);
    }

    private void createExecutionCounter() {
        executionCounter = new ExecutionCounter();
    }

    private void resetTree(Node newRootNode) {
        rootNode.removeAllChildren();
        rootNode.setUserObject(newRootNode);

        resetStatusLine();
    }

    private void configureActions() {

        createExecuteDocumentAction();
        actionGroup.addSeparator();
        createVersionSwitcherAction();
        actionGroup.addSeparator();
        createOpenDocumentAction();
        actionGroup.addSeparator();
        createRefreshRepositoryAction();

        toolBar.updateActionsImmediately();

        // Context menu with the plugin actions.
        getRepositoryTree().addMouseListener(new PopupHandler() {

            @Override
            public void invokePopup(final Component comp, final int x, final int y) {

                ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("LivingDoc.RepositoryViewToolbar", actionGroup);
                actionPopupMenu.getComponent().show(comp, x, y);
            }
        });
    }

    private void createVersionSwitcherAction() {
        // Implemented version.
        actionGroup.add(new SwitchVersionAction(tree, false));

        // Current (working) version
        actionGroup.add(new SwitchVersionAction(tree, true));

        actionGroup.add(new TagImplementedAction(tree));
    }

    private void createOpenDocumentAction() {
        actionGroup.add(new OpenRemoteDocumentAction(tree));
    }

    private void createExecuteDocumentAction() {

        actionGroup.add(new ExecuteSpecificationAction(this, false));

        // With debug mode
        actionGroup.add(new ExecuteSpecificationAction(this, true));
    }

    private void createRefreshRepositoryAction() {

        refreshAction = new AnAction() {

            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {

                resetTree(getDefaultRootNode());

                loadRepositories();
            }
        };
        refreshAction.getTemplatePresentation().setIcon(AllIcons.Actions.Refresh);
        refreshAction.getTemplatePresentation().setDescription(I18nSupport.getValue("toolwindows.action.refresh.tooltip"));
        refreshAction.getTemplatePresentation().setText(I18nSupport.getValue("toolwindows.action.refresh.tooltip"));
        actionGroup.add(refreshAction);
    }

    private void loadRepositories() {

        for (Module module : ModuleManager.getInstance(project).getModules()) {

            ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            if (moduleSettings.isLivingDocEnabled()) {

                ModuleNode moduleNode = new ModuleNode(
                        module.getName() + " [" + StringUtils.defaultIfBlank(moduleSettings.getSud(),
                                I18nSupport.getValue("toolwindows.error.loading.repositories.noproject")) + "]",
                        module.getName());
                moduleNode.setSystemUnderTest(moduleSettings.getSud());
                moduleNode.setProject(moduleSettings.getProject());

                DefaultMutableTreeNode moduleTreeNode = new DefaultMutableTreeNode(moduleNode);
                rootNode.add(moduleTreeNode);

                loadSystemUnderTests(moduleNode, moduleTreeNode);
            }
        }
        treeModel.reload();
    }

    private void loadSystemUnderTests(ModuleNode moduleNode, DefaultMutableTreeNode moduleTreeNode) {

        LivingDocConnector livingDocConnector = LivingDocConnector.newInstance(ProjectSettings.getInstance(project));

        try {
            Collection<RepositoryNode> repositories = livingDocConnector.getRepositoriesForSystemUnderTest(moduleNode);

            for (RepositoryNode repositoryNode : repositories) {

                repositoryNode.setParent(moduleNode);

                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                moduleTreeNode.add(childNode);

                livingDocConnector.buildfSpecificationHierarchy(repositoryNode, moduleNode, childNode);
            }
        } catch (LivingDocException lde) {
            LOG.error(lde);
            resetTree(RepositoryViewUtils.getErrorNode(I18nSupport.getValue("toolwindows.error.loading.repositories")
                    + lde.getMessage()));

        } catch (HttpClientErrorException hcee) {
            LOG.warn(hcee);
            resetTree(RepositoryViewUtils.getErrorNode(I18nSupport.getValue("toolwindows.error.loading.repositories.unauthorized")
                    + hcee.getMessage()));

        } catch (HttpServerErrorException hsee) {
            LOG.error(hsee);
            resetTree(RepositoryViewUtils.getErrorNode(I18nSupport.getValue("toolwindows.error.loading.repositories.internal")
                    + hsee.getMessage()));
        }
    }

    private Node getDefaultRootNode() {
        return new Node(project.getName() /*+ " [" + ldProject.getSystemUnderTest().getName() + "]"*/,
                AllIcons.Nodes.Project, NodeType.PROJECT, null);
    }
}
