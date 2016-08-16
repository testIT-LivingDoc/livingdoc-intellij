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
 * {@link #reloadTree()}.<br>
 * To add new actions, use {@link #getActionGroup()}
 * @see SimpleToolWindowPanel
 */
public class RepositoryViewUI extends SimpleToolWindowPanel {

    private static final long serialVersionUID = 3126369479423241802L;
    private final JPanel mainContent;
    private final DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private ActionToolbar toolBar;
    private DefaultActionGroup actionGroup;
    private CounterPanel counterPanel;
    private ProgressBar progressBar;
    private SimpleTree tree;


    public RepositoryViewUI(RootNode rootTreeNode) {
        super(false);

        mainContent = new JPanel(new BorderLayout());
        setContent(mainContent);

        this.rootNode = new DefaultMutableTreeNode(rootTreeNode);

        createRepositoryTree();
        createActionToolBar();
        createCounterPanel();
    }

    public void resetTree(RootNode newRootNode) {
        rootNode.removeAllChildren();
        rootNode.setUserObject(newRootNode);
    }

    public void reloadTree() {
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

    public void paintDocumentNode(java.util.List<DocumentNode> children, DefaultMutableTreeNode parentNode) {

        for (DocumentNode child : children) {

            Node node = new Node(child, (LDNode) parentNode.getUserObject());

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

    private void createActionToolBar() {

        ActionManager actionManager = ActionManager.getInstance();
        actionGroup = new DefaultActionGroup();
        toolBar = actionManager.createActionToolbar("LivingDoc.RepositoryViewToolbar", actionGroup, false);
        toolBar.adjustTheSameSize(true);
        toolBar.setTargetComponent(tree);
        setToolbar(toolBar.getComponent());
    }

    private void createRepositoryTree() {

        tree = new SimpleTree();
        tree.setCellRenderer(new LDTreeCellRenderer());
        tree.setRootVisible(true);

        // Basic functionality with single selection, desired multiple selection.
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        treeModel = new DefaultTreeModel(rootNode, true);
        tree.setModel(treeModel);
        mainContent.add(tree, BorderLayout.CENTER);
    }

    private void createCounterPanel() {

        counterPanel = new CounterPanel();
        progressBar = new ProgressBar();

        JPanel jPanel = new JPanel(new VerticalLayout(0));
        jPanel.add(counterPanel);
        jPanel.add(progressBar);

        mainContent.add(jPanel, BorderLayout.NORTH);
    }
}
