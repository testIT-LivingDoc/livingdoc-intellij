package info.novatec.testit.livingdoc.intellij.gui.toolwindows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import info.novatec.testit.livingdoc.intellij.domain.*;
import info.novatec.testit.livingdoc.intellij.gui.settings.ModuleSettings;
import info.novatec.testit.livingdoc.intellij.gui.toolwindows.action.ExecuteDocumentAction;
import info.novatec.testit.livingdoc.intellij.gui.toolwindows.action.OpenRemoteDocumentAction;
import info.novatec.testit.livingdoc.intellij.gui.toolwindows.action.SwitchVersionAction;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Set;

/**
 * Factory class for tool window configured in plugin.xml with id="Repository View".
 * The LivingDoc project configuration must be correct.
 * This class is a controller fot {@link RepositoryViewUI} view.
 *
 * @see ToolWindowFactory
 */
public class ToolWindowLivingDoc implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(ToolWindowLivingDoc.class);

    private RepositoryViewUI repositoryViewUI;

    private Project project;


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        this.project = project;

        loadView();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(repositoryViewUI, project.getName(), false);
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

        PluginLivingDocXmlRpcClient service = new PluginLivingDocXmlRpcClient();

        for (Module module : ModuleManager.getInstance(project).getModules()) {

            ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            if (moduleSettings.isLivingDocEnabled()) {

                ModuleNode moduleNode = new ModuleNode(
                        module.getName() + " [" + moduleSettings.getSud() + "]",
                        module.getName());
                DefaultMutableTreeNode moduleTreeNode = new DefaultMutableTreeNode(moduleNode);
                repositoryViewUI.getRootNode().add(moduleTreeNode);

                SystemUnderTest systemUnderTest = SystemUnderTest.newInstance(moduleSettings.getSud());
                systemUnderTest.setProject(info.novatec.testit.livingdoc.server.domain.Project.newInstance(moduleSettings.getProject()));

                try {
                    Set<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(systemUnderTest);

                    for (Repository repository : repositories) {

                        RepositoryNode repositoryNode;

                        if (validateCredentials(repository, moduleSettings)) {
                            repositoryNode = new RepositoryNode(repository.getProject().getName(), moduleNode);
                            repositoryNode.setRepository(repository);
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                            moduleTreeNode.add(childNode);

                            DocumentNode documentNode = service.getSpecificationHierarchy(repository, systemUnderTest);
                            paintDocumentNode(documentNode.getChildren(), childNode);

                        } else {
                            moduleTreeNode.add(new DefaultMutableTreeNode(getErrorNode(I18nSupport.getValue("repository.view.error.credentials"))));
                        }
                    }
                } catch (LivingDocServerException ldse) {
                    LOG.error(ldse);
                    repositoryViewUI.resetTree(getErrorNode(I18nSupport.getValue("repository.view.error.loading.repositories")
                            + ldse.getMessage()));
                }
            }
        }
        repositoryViewUI.reloadTree();
    }

    private Node getErrorNode(final String descError) {
        return new Node(descError, AllIcons.Nodes.ErrorIntroduction, NodeType.ERROR, null);
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
    private SpecificationNode convertDocumentoNodeToLDNode(final DocumentNode childNode, final Node userObject) {

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

            SpecificationNode ldNode = convertDocumentoNodeToLDNode(child, (Node) parentNode.getUserObject());
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
     * @param repository {@link Repository}
     * @return True if the credentials are valid. Otherwise, false.
     */
    private boolean validateCredentials(final Repository repository, final ModuleSettings moduleSettings) {

        boolean result = true;

        if (!StringUtils.equals(moduleSettings.getUser(), repository.getUsername())
                || !StringUtils.equals(moduleSettings.getPassword(), repository.getPassword())) {

            result = false;
        }
        return result;
    }

}
