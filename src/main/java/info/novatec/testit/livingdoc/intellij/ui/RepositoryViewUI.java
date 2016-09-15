package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.execution.testframework.ui.TestStatusLine;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.progress.util.ColorProgressBar;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.domain.RootNode;
import info.novatec.testit.livingdoc.intellij.ui.renderer.LDTreeCellRenderer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;


/**
 * User interface for LivingDoc Repository View.<br>
 * If you modify the repository tree through {@link #getRootNode()}, may be necessary to refresh the domain with
 * {@link #reloadTree()}.<br>
 * To add new actions, use {@link #getActionGroup()}
 *
 * @see SimpleToolWindowPanel
 */
public class RepositoryViewUI extends SimpleToolWindowPanel {

    private static final long serialVersionUID = 3126369479423241802L;
    private final JBPanel mainContent;
    private final DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private ActionToolbar toolBar;
    private DefaultActionGroup actionGroup;
    private SimpleTree tree;
    private TestStatusLine statusLine;


    public RepositoryViewUI(RootNode rootTreeNode) {
        super(false);

        mainContent = new JBPanel(new BorderLayout());
        mainContent.setAutoscrolls(true);
        setContent(mainContent);

        this.rootNode = new DefaultMutableTreeNode(rootTreeNode);

        createRepositoryTree();
        createActionToolBar();
        createStatusLine();
    }

    public void resetTree(RootNode newRootNode) {
        rootNode.removeAllChildren();
        rootNode.setUserObject(newRootNode);

        resetStatusLine();
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

    public SimpleTree getRepositoryTree() {
        return tree;
    }

    public TestStatusLine getStatusLine() {
        return statusLine;
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
        tree.setCellRenderer(new LDTreeCellRenderer());
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
}
