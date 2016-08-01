package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.domain.LDNode;
import info.novatec.testit.livingdoc.intellij.domain.Node;
import info.novatec.testit.livingdoc.intellij.domain.RootNode;
import info.novatec.testit.livingdoc.intellij.ui.renderer.LDTreeCellRenderer;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

/**
 * User interface for LivingDoc Repository View.<br>
 * If you modify the repository tree through {@link #getRootNode()}, may be necessary to refresh the domain with
 * {@link #reload()}.<br>
 * To add new new actions, you can do it with {@link #getActionGroup()} and adding the actions.
 */
public class RepositoryViewUI extends SimpleToolWindowPanel {

    private static final long serialVersionUID = 3126369479423241802L;

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private ActionToolbar toolBar;
    private DefaultActionGroup actionGroup;
    private JPanel mainContent;
    private CounterPanel counterPanel;
    private SimpleTree tree;

    public RepositoryViewUI(final boolean withError, final String nodeText) {

        super(false);

        mainContent = new JPanel(new BorderLayout());
        setContent(mainContent);

        initializeRootNode(withError, nodeText);

        configureRepositoryTree();
        configureActionToolBar();
        configureCounterPanel();
    }

    public void reload() {
        treeModel.reload();
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public ActionToolbar getActionToolBar() {
        return toolBar;
    }

    public DefaultActionGroup getActionGroup() {
        return actionGroup;
    }

    public CounterPanel getCounterPanel() {
        return counterPanel;
    }

    public SimpleTree getRepositoryTree() {
        return tree;
    }

    public void initializeRootNode(final boolean withError, final String nodeText) {
        Icon icon = Icons.PROJECT;
        if (withError) {
            icon = Icons.ERROR;
        }
        RootNode ideaProjectNode = new RootNode(nodeText, icon);

        if (rootNode == null) {
            rootNode = new DefaultMutableTreeNode(ideaProjectNode);
        } else {
            rootNode.setUserObject(ideaProjectNode);
        }
    }

    public void paintDocumentNode(java.util.List<DocumentNode> children, DefaultMutableTreeNode parentNode) {

        for (DocumentNode child : children) {

            Node node = new Node(child, (LDNode) parentNode.getUserObject(), Icons.EXECUTABLE);

            if (node.isExecutable() && node.canBeImplemented()) {
                node.setIcon(Icons.EXE_DIFF);

                // TODO node.isUsingCurrentVersion() not implemented
                //} else if(node.isExecutable() && isUsingCurrentVersion()) {
                //    node.setIcon(Icons.EXE_WORKING);

            } else if (!node.isExecutable()) {
                node.setIcon(Icons.NOT_EXECUTABLE);
            }

            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node);
            parentNode.add(childNode);

            if (child.hasChildren()) {
                paintDocumentNode(child.getChildren(), childNode);
            }
        }
    }

    private void configureActionToolBar() {
        ActionManager actionManager = ActionManager.getInstance();
        actionGroup = new DefaultActionGroup();
        toolBar = actionManager.createActionToolbar("LivingDoc.RepositoryViewToolbar",
                actionGroup, false);
        toolBar.adjustTheSameSize(true);
        //toolBar.setTargetComponent(tree);
        setToolbar(toolBar.getComponent());
    }

    private void configureRepositoryTree() {

        tree = new SimpleTree();
        tree.setCellRenderer(new LDTreeCellRenderer());
        tree.setRootVisible(true);

        // Basic functionality with single selection, desired multiple selection.
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        treeModel = new DefaultTreeModel(rootNode, true);
        tree.setModel(treeModel);
        mainContent.add(tree, BorderLayout.CENTER);
    }

    private void configureCounterPanel() {

        counterPanel = new CounterPanel();

        JPanel jPanel = new JPanel(new VerticalLayout(0));
        jPanel.add(counterPanel);
        jPanel.add(new ProgressBar());

        mainContent.add(jPanel, BorderLayout.NORTH);
    }
}
