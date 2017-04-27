package info.novatec.testit.livingdoc.intellij.gui.toolwindows.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.common.I18nSupport;
import info.novatec.testit.livingdoc.intellij.common.Icons;
import info.novatec.testit.livingdoc.intellij.domain.SpecificationNode;
import info.novatec.testit.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;
import info.novatec.testit.livingdoc.repository.DocumentRepository;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The action sets the selected(s) document as implemented in the remote server.<br>
 * See {@link #update(AnActionEvent)} for the display restrictions.
 *
 * @see AnAction
 */
public class TagImplementedAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(TagImplementedAction.class);

    private final SimpleTree repositoryTree;


    /**
     * @param tree LivingDoc repository tree.
     */
    public TagImplementedAction(final SimpleTree tree) {

        super(I18nSupport.getValue("toolwindows.action.tag.tooltip"),
                I18nSupport.getValue("toolwindows.action.tag.tooltip"),
                AllIcons.Actions.GroupByPackage);

        this.repositoryTree = tree;
    }

    /**
     * Action handler.
     * TODO NOTE: Basic functionality with single selection, desired multiple selection.
     *
     * @param anActionEvent Carries information on the invocation place
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        DefaultMutableTreeNode[] nodes = repositoryTree.getSelectedNodes(DefaultMutableTreeNode.class, null);

        for (DefaultMutableTreeNode selectedNode : nodes) {
            Object userObject = selectedNode.getUserObject();
            SpecificationNode specificationNode = (SpecificationNode) userObject;

            try {
                DocumentRepository documentRepository =
                        RepositoryViewUtils.getRepositoryNode(specificationNode)
                                .getRepository().asDocumentRepository(getClass().getClassLoader());
                documentRepository.setDocumentAsImplemented(specificationNode.getName());

                specificationNode.setUsingCurrentVersion(false);
                specificationNode.setCanBeImplemented(false);
                specificationNode.setIcon(Icons.EXECUTABLE);

            } catch (Exception e) {
                LOG.error(e);
                selectedNode.setUserObject(RepositoryViewUtils.getErrorNode(specificationNode.getName() + " (" + e.getMessage() + ")"));
            }
        }
        repositoryTree.getSelectionModel().clearSelection();
    }

    /**
     * This action will be enabled only for executable nodes with working version.
     *
     * @param anActionEvent Carries information on the invocation place
     */
    @Override
    public void update(AnActionEvent anActionEvent) {

        super.update(anActionEvent);

        DefaultMutableTreeNode[] selectedNodes = repositoryTree.getSelectedNodes(DefaultMutableTreeNode.class, null);
        RepositoryViewUtils.setEnabledForNodeVersion(selectedNodes, anActionEvent.getPresentation(), false);
    }
}
