package info.novatec.testit.livingdoc.intellij.gui.toolwindows.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.browsers.BrowserLauncher;
import com.intellij.ide.browsers.BrowserLauncherImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.common.I18nSupport;
import info.novatec.testit.livingdoc.intellij.common.NodeType;
import info.novatec.testit.livingdoc.intellij.domain.Node;
import info.novatec.testit.livingdoc.intellij.domain.RepositoryNode;
import info.novatec.testit.livingdoc.intellij.domain.SpecificationNode;
import info.novatec.testit.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;
import info.novatec.testit.livingdoc.server.domain.Specification;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The action shows the selected document(s) from the Confluence repository in the web browser.
 * A web browser tab is opened for each selected document.<br>
 * See {@link #update(AnActionEvent)} for the display restrictions.
 *
 * @see AnAction
 */
public class OpenRemoteDocumentAction extends AnAction {

    private final SimpleTree repositoryTree;

    /**
     * Creates the action with its text, description and icon.
     *
     * @param tree LivingDoc repository tree.
     */
    public OpenRemoteDocumentAction(final SimpleTree tree) {

        super(I18nSupport.getValue("toolwindows.action.open.tooltip"),
                I18nSupport.getValue("toolwindows.action.open.tooltip"),
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

        if (((Node) userObject).getType() == NodeType.SPECIFICATION) {

            SpecificationNode specificationNode = (SpecificationNode) userObject;
            RepositoryNode repositoryNode = RepositoryViewUtils.getRepositoryNode(specificationNode);

            BrowserLauncher browser = new BrowserLauncherImpl();

            for (DefaultMutableTreeNode selectedNode : selectedNodes) {

                userObject = selectedNode.getUserObject();

                specificationNode = (SpecificationNode) userObject;
                Specification specification = Specification.newInstance(specificationNode.getName());
                specification.setRepository(repositoryNode.getRepository());

                browser.open(specification.getRepository().getType().resolveName(specification));
            }
        }
    }

    /**
     * This action will be enabled only for specification nodes {@link NodeType}
     *
     * @param actionEvent Carries information on the invocation place
     */
    @Override
    public void update(AnActionEvent actionEvent) {

        super.update(actionEvent);

        DefaultMutableTreeNode[] selectedNodes = repositoryTree.getSelectedNodes(DefaultMutableTreeNode.class, null);
        RepositoryViewUtils.setEnabledForSpecificationNode(selectedNodes, actionEvent.getPresentation());
    }
}
