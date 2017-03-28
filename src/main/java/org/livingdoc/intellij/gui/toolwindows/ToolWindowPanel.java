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
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.apache.commons.lang3.StringUtils;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.common.NodeType;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.gui.toolwindows.action.ExecuteDocumentAction;
import org.livingdoc.intellij.gui.toolwindows.action.OpenRemoteDocumentAction;
import org.livingdoc.intellij.gui.toolwindows.action.SwitchVersionAction;
import org.livingdoc.intellij.gui.toolwindows.action.TagImplementedAction;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;


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

        configureActions();

        loadRepositories();
    }

    public SimpleTree getRepositoryTree() {
        return this.tree;
    }

    public TestStatusLine getStatusLine() {
        return this.statusLine;
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

        // Basic functionality with single selection, desired multiple selection.
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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

        actionGroup.add(new ExecuteDocumentAction(this, false));

        // With debug mode
        actionGroup.add(new ExecuteDocumentAction(this, true));
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
                DefaultMutableTreeNode moduleTreeNode = new DefaultMutableTreeNode(moduleNode);
                rootNode.add(moduleTreeNode);

                loadSystemUnderTests(moduleSettings, moduleNode, moduleTreeNode);
            }
        }
        treeModel.reload();
    }

    private void loadSystemUnderTests(ModuleSettings moduleSettings, ModuleNode moduleNode, DefaultMutableTreeNode moduleTreeNode) {

        LivingDocConnector livingDocConnector = LivingDocConnector.newInstance(ProjectSettings.getInstance(project));

        SystemUnderTest systemUnderTest = SystemUnderTest.newInstance(moduleSettings.getSud());
        systemUnderTest.setProject(info.novatec.testit.livingdoc.server.domain.Project.newInstance(moduleSettings.getProject()));

        try {
            Set<Repository> repositories = livingDocConnector.getAllRepositoriesForSystemUnderTest(systemUnderTest);

            for (Repository repository : repositories) {

                RepositoryNode repositoryNode;
                repositoryNode = new RepositoryNode(repository.getProject().getName(), moduleNode);
                repositoryNode.setRepository(repository);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                moduleTreeNode.add(childNode);

                DocumentNode documentNode = livingDocConnector.getSpecificationHierarchy(repository, systemUnderTest);
                paintDocumentNode(documentNode.getChildren(), childNode);
            }
        } catch (LivingDocServerException ldse) {
            LOG.error(ldse);
            resetTree(RepositoryViewUtils.getErrorNode(I18nSupport.getValue("toolwindows.error.loading.repositories")
                    + ldse.getMessage()));

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

    /**
     * @param childNode  {@link DocumentNode}
     * @param userObject {@link Node}
     * @return {@link SpecificationNode}
     */
    private SpecificationNode convertDocumentNodeToLDNode(final DocumentNode childNode, final Node userObject) {

        SpecificationNode specificationNode = new SpecificationNode(childNode, userObject);
        specificationNode.setIcon(RepositoryViewUtils.getNodeIcon(specificationNode));
        return specificationNode;
    }

    /**
     * This recursive method adds a node into the repository tree.<br>
     * Only the executable nodes or nodes with children will be painted.
     *
     * @param children   {@link java.util.List}
     * @param parentNode {@link DefaultMutableTreeNode} Parent node of children nodes indicated in the first parameter.
     * @see DocumentNode
     */
    private void paintDocumentNode(java.util.List<DocumentNode> children, DefaultMutableTreeNode parentNode) {

        children.stream().filter(child -> child.isExecutable() || (!child.isExecutable() && child.hasChildren())).forEach(child -> {

            SpecificationNode ldNode = convertDocumentNodeToLDNode(child, (Node) parentNode.getUserObject());
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(ldNode);
            parentNode.add(childNode);

            if (child.hasChildren()) {
                paintDocumentNode(child.getChildren(), childNode);
            }
        });
        sortChildren(parentNode);
    }

    private void sortChildren(DefaultMutableTreeNode node) {

        List<DefaultMutableTreeNode> childrenList = Collections.list(node.children());

        childrenList.sort((o1, o2) ->
                ((Node) o1.getUserObject()).getName().compareToIgnoreCase(((Node) o2.getUserObject()).getName()));

        node.removeAllChildren();
        childrenList.forEach(node::add);
    }
}
