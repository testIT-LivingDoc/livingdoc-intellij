package info.novatec.testit.livingdoc.intellij.action.repository;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import info.novatec.testit.livingdoc.intellij.domain.*;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.ui.RepositoryViewUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Set;

/**
 * Factory class for tool window configured in plugin.xml with id="Repository View".
 * The LivingDoc project configuration must be correct. (See {@link LDProject})
 * This class is a controller fot {@link RepositoryViewUI} view.
 *
 * @see ToolWindowFactory
 */
public class RepositoryViewController implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance("#info.novatec.testit.livingdoc.intellij.action.repository.RepositoryViewController");

    private RepositoryViewUI repositoryViewUI;
    private LDProject ldProject;

    /**
     * Recursive method to find the specificationNode's repository through specificationNode's parent.
     *
     * @param specificationNode {@link SpecificationNode} Livingdoc specification specificationNode
     * @return @{@link RepositoryNode}
     */
    public static RepositoryNode getRepositoryNode(final SpecificationNode specificationNode) {
        if (specificationNode.getParent() instanceof RepositoryNode) {
            return (RepositoryNode) specificationNode.getParent();
        } else {
            return getRepositoryNode((SpecificationNode) specificationNode.getParent());
        }
    }

    @Override
    public void createToolWindowContent(@NotNull Project ideaProject, @NotNull ToolWindow toolWindow) {

        ldProject = new LDProject(ideaProject);

        loadView();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(repositoryViewUI, ldProject.getIdeaProject().getName(), false);
        content.setIcon(Icons.LIVINGDOC);
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
    }

    private void loadView() {

        repositoryViewUI = new RepositoryViewUI(getDefaultRootNode());

        configureActions();

        loadRepositories();
    }

    private void configureActions() {

        createExecuteDocumentAction();
        repositoryViewUI.getActionGroup().addSeparator();
        createVersionSwitcherAction();
        repositoryViewUI.getActionGroup().addSeparator();
        createOpenDocumentAction();
        repositoryViewUI.getActionGroup().addSeparator();
        createRefreshRepositoryAction();

        repositoryViewUI.getActionToolBar().updateActionsImmediately();

        // Context menu with the plugin actions.
        repositoryViewUI.getRepositoryTree().addMouseListener(new PopupHandler() {

            @Override
            public void invokePopup(final Component comp, final int x, final int y) {

                ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("LivingDoc.RepositoryViewToolbar", repositoryViewUI.getActionGroup());
                actionPopupMenu.getComponent().show(comp, x, y);
            }
        });
    }

    private void createVersionSwitcherAction() {
        SwitchVersionAction implementedVersionAction = new SwitchVersionAction(repositoryViewUI.getRepositoryTree(), false);
        repositoryViewUI.getActionGroup().add(implementedVersionAction);

        // Current version
        SwitchVersionAction workingsVersionAction = new SwitchVersionAction(repositoryViewUI.getRepositoryTree(), true);
        repositoryViewUI.getActionGroup().add(workingsVersionAction);
    }

    private void createOpenDocumentAction() {
        OpenRemoteDocumentAction openRemoteDocumentAction = new OpenRemoteDocumentAction(repositoryViewUI.getRepositoryTree());
        repositoryViewUI.getActionGroup().add(openRemoteDocumentAction);
    }

    private void createExecuteDocumentAction() {
        ExecuteDocumentAction executeDocumentAction = new ExecuteDocumentAction(repositoryViewUI, false);
        repositoryViewUI.getActionGroup().add(executeDocumentAction);

        // With debug mode
        ExecuteDocumentAction debugDocumentAction = new ExecuteDocumentAction(repositoryViewUI, true);
        repositoryViewUI.getActionGroup().add(debugDocumentAction);
    }

    private void createRefreshRepositoryAction() {

        AnAction anAction = new AnAction() {

            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {

                ldProject.reload();

                repositoryViewUI.resetTree(getDefaultRootNode());

                loadRepositories();
            }
        };
        anAction.getTemplatePresentation().setIcon(AllIcons.Actions.Refresh);
        anAction.getTemplatePresentation().setDescription(I18nSupport.getValue("repository.view.action.refresh.tooltip"));
        anAction.getTemplatePresentation().setText(I18nSupport.getValue("repository.view.action.refresh.tooltip"));
        repositoryViewUI.getActionGroup().add(anAction);
    }

    private void loadRepositories() {

        if (!ldProject.isConfiguredProject()) {
            repositoryViewUI.resetTree(getErrorRootNode(I18nSupport.getValue("repository.view.error.project.not.configured")));
            return;
        }

        try {
            RpcClientService service = new PluginLivingDocXmlRpcClient();
            Set<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(ldProject.getSystemUnderTest(),
                    ldProject.getIdentifier());

            for (Repository repository : repositories) {

                RepositoryNode repositoryNode;

                if (validateCredentials(ldProject, repository)) {
                    repositoryNode = new RepositoryNode(repository.getProject().getName());
                    repositoryNode.setRepository(repository);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                    repositoryViewUI.getRootNode().add(childNode);

                    DocumentNode documentNode = service.getSpecificationHierarchy(repository,
                            ldProject.getSystemUnderTest(), ldProject.getIdentifier());

                    paintDocumentNode(documentNode.getChildren(), childNode);

                } else {

                    Messages.showErrorDialog(ldProject.getIdeaProject(),

                            I18nSupport.getValue("repository.view.error.credentials"),
                            I18nSupport.getValue("repository.view.error.loading.repositories"));

                    repositoryNode = new RepositoryNode(I18nSupport.getValue("repository.view.error.credentials"));
                    repositoryNode.setIcon(Icons.ERROR);
                    repositoryViewUI.getRootNode().add(new DefaultMutableTreeNode(repositoryNode));
                }
            }

            repositoryViewUI.reloadTree();

        } catch (LivingDocServerException ldse) {
            LOG.error(ldse);
            Messages.showErrorDialog(ldProject.getIdeaProject(), ldse.getMessage(), I18nSupport.getValue("repository.view.error.loading.repositories"));

            repositoryViewUI.resetTree(getErrorRootNode(ldse.getMessage()));
        }
    }

    private RootNode getErrorRootNode(final String descError) {
        RootNode rootNode = new RootNode(descError);
        rootNode.setIcon(Icons.ERROR);
        return rootNode;
    }

    private RootNode getDefaultRootNode() {
        return new RootNode(ldProject.getIdeaProject().getName() + " [" + ldProject.getSystemUnderTest().getName() + "]");
    }

    /**
     * @param childNode  {@link DocumentNode}
     * @param userObject {@link LDNode}
     * @return {@link SpecificationNode}
     */
    private SpecificationNode convertDocumentoNodeToLDNode(final DocumentNode childNode, final LDNode userObject) {

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

            SpecificationNode ldNode = convertDocumentoNodeToLDNode(child, (LDNode) parentNode.getUserObject());
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(ldNode);
            parentNode.add(childNode);

            if (child.hasChildren()) {
                paintDocumentNode(child.getChildren(), childNode);
            }
        });
    }

    /**
     * Validates the LivingDoc user and password configured in IntelliJ to connect with LivingDoc repository.
     *
     * @param ldProject  {@link LDProject}
     * @param repository {@link Repository}
     * @return True if the credentials are valid. Otherwise, false.
     */
    private boolean validateCredentials(final LDProject ldProject, final Repository repository) {

        boolean result = true;

        if (!StringUtils.equals(ldProject.getUser(), repository.getUsername())
                || !StringUtils.equals(ldProject.getPass(), repository.getPassword())) {

            result = false;
        }

        return result;
    }

}
