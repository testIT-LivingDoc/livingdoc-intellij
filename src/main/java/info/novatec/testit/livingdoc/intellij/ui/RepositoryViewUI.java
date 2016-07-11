package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.SimpleTree;
import info.novatec.testit.livingdoc.intellij.model.LDNode;
import info.novatec.testit.livingdoc.intellij.model.LDProject;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.ui.renderer.LDTreeCellRenderer;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.intellij.util.UIUtils;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Factory class for tool window configured in plugin.xml with id="Repository View"
 * The LivingDoc project configuration must be correct. (See {@link LDProject})
 * @see ToolWindowFactory
 */
public class RepositoryViewUI implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(RepositoryViewUI.class);

    private DefaultMutableTreeNode rootNode;
    private LDProject ldProject;
    private ToolWindow toolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project IDEAProject, @NotNull ToolWindow tw) {

        toolWindow = tw;

        ldProject = new LDProject(IDEAProject);

        initializeRootNode(!ldProject.isConfiguredProject(),
                I18nSupport.getValue("repository.view.error.project.not.configured"));

        if(ldProject.isConfiguredProject()) {
            loadRepositories();
        }

        addComponents();
    }

    private void initializeRootNode(final boolean isError, final String descError) {

        if(!isError) {
            LDNode ideaProjectNode = new LDNode(ldProject.getIdeaProject().getName(), Icons.PROJECT);
            rootNode = new DefaultMutableTreeNode(ideaProjectNode);

        } else {
            LDNode errorNode = new LDNode(descError, Icons.ERROR);
            rootNode = new DefaultMutableTreeNode(errorNode);
        }
    }

    private void addComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new FlowLayout());

        JBTextField rightsField = new JBTextField("0");
        rightsField.setEditable(false);
        rightsField.setHorizontalAlignment(SwingConstants.LEFT);
        rightsField.setColumns(10);
        JBLabel rightsLabel = new JBLabel(I18nSupport.getValue("repository.view.field.rights.label"));
        rightsLabel.setLabelFor(rightsField);
        fieldsPanel.add(rightsLabel);
        fieldsPanel.add(rightsField);

        JBTextField wrongsField = new JBTextField("0");
        wrongsField.setEditable(false);
        wrongsField.setHorizontalAlignment(SwingConstants.LEFT);
        wrongsField.setColumns(10);
        JBLabel wrongsLabel = new JBLabel(I18nSupport.getValue("repository.view.field.wrongs.label"));
        wrongsLabel.setLabelFor(wrongsField);
        fieldsPanel.add(wrongsLabel);
        fieldsPanel.add(wrongsField);

        JBTextField errorsField = new JBTextField("0");
        errorsField.setEditable(false);
        errorsField.setHorizontalAlignment(SwingConstants.LEFT);
        errorsField.setColumns(10);
        JBLabel errorsLabel = new JBLabel(I18nSupport.getValue("repository.view.field.errors.label"));
        errorsLabel.setLabelFor(errorsField);
        fieldsPanel.add(errorsLabel);
        fieldsPanel.add(errorsField);

        mainPanel.add(fieldsPanel, BorderLayout.NORTH);

        SimpleTree tree = new SimpleTree();
        tree.setCellRenderer(new LDTreeCellRenderer());
        tree.setRootVisible(true);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode, true);
        tree.setModel(treeModel);
        mainPanel.add(tree, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel,
                ldProject.getIdeaProject().getName() + " [" + ldProject.getSystemUnderTest().getName() + "]", false);
        content.setIcon(Icons.LIVINGDOC);
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
        content.setCloseable(true);

        toolWindow.getContentManager().addContent(content);
    }

    private void loadRepositories() {

        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(ldProject.getSystemUnderTest(),
                    ldProject.getIdentifier());

            for (Repository repository : repositories) {

                if(UIUtils.validateCredentials(ldProject,repository)) {
                    LDNode repositoryNode = new LDNode(repository.getProject().getName(), Icons.REPOSITORY);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                    rootNode.add(childNode);

                    DocumentNode documentNode = service.getSpecificationHierarchy(repository,
                            ldProject.getSystemUnderTest(), ldProject.getIdentifier());

                    paintDocumentNode(documentNode.getChildren(), childNode);

                } else {
                    Messages.showErrorDialog(ldProject.getIdeaProject(),
                            I18nSupport.getValue("repository.view.error.credentials"),
                            I18nSupport.getValue("repository.view.error.loading.repositories"));
                }
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(ldProject.getIdeaProject(), ldse.getMessage(), I18nSupport.getValue("repository.view.error.loading.repositories"));
            LOG.error(ldse);

            initializeRootNode(true, ldse.getMessage());
        }
    }

    private void paintDocumentNode(List<DocumentNode> children, DefaultMutableTreeNode parentNode) {

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
}
