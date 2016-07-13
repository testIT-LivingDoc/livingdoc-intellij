package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import info.novatec.testit.livingdoc.intellij.model.LDNode;
import info.novatec.testit.livingdoc.intellij.model.LDProject;
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
 * Factory class for tool window configured in plugin.xml with id="Repository View"
 * The LivingDoc project configuration must be correct. (See {@link LDProject})
 * This class is a controller fot {@link RepositoryViewUI} view.
 * @see ToolWindowFactory
 */
public class RepositoryViewController implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(RepositoryViewController.class);

    private RepositoryViewUI repositoryViewUI;
    private LDProject ldProject;

    @Override
    public void createToolWindowContent(@NotNull Project IDEAProject, @NotNull ToolWindow toolWindow) {

        ldProject = new LDProject(IDEAProject);

        if (ldProject.isConfiguredProject()) {

            repositoryViewUI = new RepositoryViewUI(false, ldProject.getIdeaProject().getName());

            loadRepositories();

        } else {
            repositoryViewUI = new RepositoryViewUI(true,
                    I18nSupport.getValue("repository.view.error.project.not.configured"));
        }

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

        configureRefreshRepositoryAction();
        repositoryViewUI.getActionGroup().addSeparator();
        configureExecuteDocumentAction();

        repositoryViewUI.getActionToolBar().updateActionsImmediately();
    }

    private void configureExecuteDocumentAction() {
        AnAction anAction = new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                // TODO
                Messages.showInfoMessage("Not implemented","Execute Document");
            }
        };
        anAction.getTemplatePresentation().setIcon(Icons.EXECUTE);
        repositoryViewUI.getActionGroup().add(anAction);
    }

    private void configureRefreshRepositoryAction() {
        AnAction anAction = new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {

                ldProject.load();

                repositoryViewUI.getRootNode().removeAllChildren();

                loadRepositories();
            }
        };
        anAction.getTemplatePresentation().setIcon(Icons.REFRESH);
        repositoryViewUI.getActionGroup().add(anAction);
    }

    private void loadRepositories() {

        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(ldProject.getSystemUnderTest(),
                    ldProject.getIdentifier());

            for (Repository repository : repositories) {

                if (UIUtils.validateCredentials(ldProject, repository)) {
                    LDNode repositoryNode = new LDNode(repository.getProject().getName(), Icons.REPOSITORY);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                    repositoryViewUI.getRootNode().add(childNode);

                    DocumentNode documentNode = service.getSpecificationHierarchy(repository,
                            ldProject.getSystemUnderTest(), ldProject.getIdentifier());

                    repositoryViewUI.paintDocumentNode(documentNode.getChildren(), childNode);

                } else {
                    Messages.showErrorDialog(ldProject.getIdeaProject(),
                            I18nSupport.getValue("repository.view.error.credentials"),
                            I18nSupport.getValue("repository.view.error.loading.repositories"));

                    repositoryViewUI.initializeRootNode(true,  I18nSupport.getValue("repository.view.error.credentials"));
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
