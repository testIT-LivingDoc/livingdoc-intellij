package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import info.novatec.testit.livingdoc.intellij.action.repository.ExecuteDocumentAction;
import info.novatec.testit.livingdoc.intellij.action.repository.OpenRemoteDocumentAction;
import info.novatec.testit.livingdoc.intellij.domain.LDProject;
import info.novatec.testit.livingdoc.intellij.domain.RepositoryNode;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.ui.RepositoryViewUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.intellij.util.UIUtils;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Set;

/**
 * Factory class for tool window configured in plugin.xml with id="Repository View".
 * The LivingDoc project configuration must be correct. (See {@link LDProject})
 * This class is a controller fot {@link RepositoryViewUI} view.
 *
 * @see ToolWindowFactory
 */
public class RepositoryViewController implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance("#info.novatec.testit.livingdoc.intellij.action.RepositoryViewController");

    private RepositoryViewUI repositoryViewUI;
    private LDProject ldProject;

    @Override
    public void createToolWindowContent(@NotNull Project IDEAProject, @NotNull ToolWindow toolWindow) {

        ldProject = new LDProject(IDEAProject);

        loadView();

        configureCounter();
        configureActions();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(repositoryViewUI,
                ldProject.getIdeaProject().getName() + " [" + ldProject.getSystemUnderTest().getName() + "]", false);
        content.setIcon(Icons.LIVINGDOC);
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
    }

    private void configureCounter() {
        repositoryViewUI.getCounterPanel().setTotal(0);
        repositoryViewUI.getCounterPanel().setRightsValue(0);
    }

    private void configureActions() {

        createRefreshRepositoryAction();
        repositoryViewUI.getActionGroup().addSeparator();
        createExecuteDocumentAction();
        repositoryViewUI.getActionGroup().addSeparator();
        createOpenDocumentAction();

        repositoryViewUI.getActionToolBar().updateActionsImmediately();
    }

    private void createOpenDocumentAction() {
        OpenRemoteDocumentAction openRemoteDocumentAction = new OpenRemoteDocumentAction(repositoryViewUI.getRepositoryTree());
        repositoryViewUI.getActionGroup().add(openRemoteDocumentAction);
    }

    private void createExecuteDocumentAction() {
        ExecuteDocumentAction executeDocumentAction = new ExecuteDocumentAction(repositoryViewUI.getRepositoryTree(), false);
        repositoryViewUI.getActionGroup().add(executeDocumentAction);

        // With debug mode
        executeDocumentAction = new ExecuteDocumentAction(repositoryViewUI.getRepositoryTree(), true);
        repositoryViewUI.getActionGroup().add(executeDocumentAction);
    }

    private void createRefreshRepositoryAction() {

        AnAction anAction = new AnAction() {

            @Override
            public void actionPerformed(AnActionEvent e) {
                ldProject.load();
                loadView();
            }
        };
        anAction.getTemplatePresentation().setIcon(AllIcons.Actions.Refresh);
        anAction.getTemplatePresentation().setDescription(I18nSupport.getValue("repository.view.action.refresh.tooltip"));
        repositoryViewUI.getActionGroup().add(anAction);
    }

    private void loadView() {

        if (ldProject.isConfiguredProject()) {

            repositoryViewUI = new RepositoryViewUI(false, ldProject.getIdeaProject().getName());

            loadRepositories();

        } else {
            repositoryViewUI = new RepositoryViewUI(true,
                    I18nSupport.getValue("repository.view.error.project.not.configured"));
        }
    }

    private void loadRepositories() {

        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(ldProject.getSystemUnderTest(),
                    ldProject.getIdentifier());

            for (Repository repository : repositories) {

                if (UIUtils.validateCredentials(ldProject, repository)) {
                    RepositoryNode repositoryNode = new RepositoryNode(repository.getProject().getName(), Icons.REPOSITORY);
                    repositoryNode.setRepository(repository);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                    repositoryViewUI.getRootNode().add(childNode);

                    DocumentNode documentNode = service.getSpecificationHierarchy(repository,
                            ldProject.getSystemUnderTest(), ldProject.getIdentifier());

                    repositoryViewUI.paintDocumentNode(documentNode.getChildren(), childNode);

                } else {
                    Messages.showErrorDialog(ldProject.getIdeaProject(),
                            I18nSupport.getValue("repository.view.error.credentials"),
                            I18nSupport.getValue("repository.view.error.loading.repositories"));

                    LOG.debug(I18nSupport.getValue("repository.view.error.loading.repositories"));

                    repositoryViewUI.initializeRootNode(true, I18nSupport.getValue("repository.view.error.credentials"));
                }
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(ldProject.getIdeaProject(), ldse.getMessage(), I18nSupport.getValue("repository.view.error.loading.repositories"));
            LOG.error(ldse);

            repositoryViewUI.initializeRootNode(true, ldse.getMessage());
        }
        repositoryViewUI.reload();
    }
}
