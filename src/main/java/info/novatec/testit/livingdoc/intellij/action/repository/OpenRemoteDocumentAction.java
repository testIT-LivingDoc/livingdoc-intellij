package info.novatec.testit.livingdoc.intellij.action.repository;

import com.intellij.icons.AllIcons;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.BrowserLauncherImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.domain.LDNode;
import info.novatec.testit.livingdoc.intellij.domain.LDNodeType;
import info.novatec.testit.livingdoc.intellij.domain.Node;
import info.novatec.testit.livingdoc.intellij.domain.RepositoryNode;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.domain.Specification;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The action shows the selected document(s) from the Confluence repository in the web browser.
 * A web browser tab is opened for each selected document.<br>
 * See {@link #update(AnActionEvent)} for the display restrictions.
 *
 * @see AnAction
 */
class OpenRemoteDocumentAction extends AnAction {

    private final SimpleTree repositoryTree;

    public OpenRemoteDocumentAction(final SimpleTree tree) {

        super(I18nSupport.getValue("repository.view.action.open.tooltip"),
                I18nSupport.getValue("repository.view.action.open.tooltip"),
                AllIcons.Javaee.WebModule);

        this.repositoryTree = tree;
    }

    /**
     * Action handler. Only specification nodes will open in the web browser.
     *
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent actionEvent) {

        DefaultMutableTreeNode[] selectedNodes = repositoryTree.getSelectedNodes(DefaultMutableTreeNode.class, null);

        Object userObject = selectedNodes[0].getUserObject();

        if (((LDNode) userObject).getType() == LDNodeType.SPECIFICATION) {

            Node node = (Node) userObject;
            RepositoryNode repositoryNode = RepositoryViewController.getRepositoryNode(node);

            BrowserLauncher browser = new BrowserLauncherImpl();

            for (DefaultMutableTreeNode selectedNode : selectedNodes) {

                userObject = selectedNode.getUserObject();

                node = (Node) userObject;
                Specification specification = Specification.newInstance(node.getName());
                specification.setRepository(repositoryNode.getRepository());

                browser.open(specification.getRepository().getType().resolveName(specification));
            }
        }
    }

    /**
     * This action will be enabled only for specification nodes {@link LDNodeType}
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void update(AnActionEvent actionEvent) {

        super.update(actionEvent);

        Presentation presentation = actionEvent.getPresentation();

        DefaultMutableTreeNode[] selectedNodes = repositoryTree.getSelectedNodes(DefaultMutableTreeNode.class, null);
        if(ArrayUtils.isEmpty(selectedNodes)) {
            presentation.setEnabled(false);
            return;
        }

        Object userObject = selectedNodes[0].getUserObject();
        presentation.setEnabled(((LDNode) userObject).getType() == LDNodeType.SPECIFICATION);
    }
}
