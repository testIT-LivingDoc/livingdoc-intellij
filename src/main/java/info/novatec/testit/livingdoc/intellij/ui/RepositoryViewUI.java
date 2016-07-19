package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.model.LDNode;
import info.novatec.testit.livingdoc.intellij.ui.renderer.LDTreeCellRenderer;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * User interface for LivingDoc Repository View.<br>
 * If you modify the repository tree through {@link #getRootNode()}, may be necessary to refresh the model with
 * {@link #reload()}.<br>
 * To add new new actions, you can do it with {@link #getActionGroup()} and adding the actions.
 */
public class RepositoryViewUI extends JPanel {

    private static final long serialVersionUID = 3126369479423241802L;

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private ActionToolbar toolBar;
    private DefaultActionGroup actionGroup;
    private CounterPanel counterPanel;
    private SimpleTree tree;

    public RepositoryViewUI(final boolean withError, final String nodeText) {

        setLayout(new BorderLayout());

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

    public CounterPanel getCounterPanel(){
        return counterPanel;
    }

    public SimpleTree getRepositoryTree() { return tree; }

    public void initializeRootNode(final boolean withError, final String nodeText) {
        Icon icon = Icons.PROJECT;
        if (withError) {
            icon = Icons.ERROR;
        }
        LDNode ideaProjectNode = new LDNode(nodeText, icon);

        if (rootNode == null) {
            rootNode = new DefaultMutableTreeNode(ideaProjectNode);
        } else {
            rootNode.setUserObject(ideaProjectNode);
        }
    }

    public void paintDocumentNode(java.util.List<DocumentNode> children, DefaultMutableTreeNode parentNode) {

        for (DocumentNode child : children) {

            LDNode ldNode = new LDNode(child, Icons.EXECUTABLE);

            if (ldNode.isExecutable() && ldNode.canBeImplemented()) {
                ldNode.setIcon(Icons.EXE_DIFF);

                // TODO isUsingCurrentVersion() not implemented
                //} else if(ldNode.isExecutable() && isUsingCurrentVersion()) {
                //    ldNode.setIcon(Icons.EXE_WORKING);

            } else if (!ldNode.isExecutable()) {
                ldNode.setIcon(Icons.NOT_EXECUTABLE);
            }

            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(ldNode);
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
                actionGroup ,false);
        toolBar.adjustTheSameSize(true);
        //toolBar.setTargetComponent(tree);

        add(toolBar.getComponent(), BorderLayout.WEST);
    }

    private void configureRepositoryTree() {
        tree = new SimpleTree();
        tree.setCellRenderer(new LDTreeCellRenderer());
        tree.setRootVisible(true);
        treeModel = new DefaultTreeModel(rootNode, true);
        tree.setModel(treeModel);
        add(tree, BorderLayout.CENTER);
    }

    private void configureCounterPanel() {

        counterPanel = new CounterPanel();

        JPanel jPanel = new JPanel(new VerticalLayout(0));
        jPanel.add(counterPanel);
        jPanel.add(new ProgressBar());

        add(jPanel, BorderLayout.NORTH);
    }
}
