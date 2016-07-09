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
 * Factory class for tool window configured in plugin.xml
 * @see ToolWindowFactory
 */
public class RepositoryViewUI implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(RepositoryViewUI.class);

    private SimpleTree tree;
    private LDProject ldProject;
    private ToolWindow toolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project IDEAProject, @NotNull ToolWindow tw) {

        ldProject = new LDProject(IDEAProject);
        toolWindow = tw;

        addComponents();

        loadRepositories();
    }

    private void addComponents() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new FlowLayout());

        fieldsPanel.add(new JBLabel(I18nSupport.getValue("repository.view.field.rights.label")));

        JBTextField rightsField = new JBTextField("0");
        rightsField.setEditable(false);
        rightsField.setHorizontalAlignment(SwingConstants.LEFT);
        rightsField.setColumns(10);
        fieldsPanel.add(rightsField);

        fieldsPanel.add(new JBLabel(I18nSupport.getValue("repository.view.field.wrongs.label")));

        JBTextField wrongsField = new JBTextField("0");
        wrongsField.setEditable(false);
        wrongsField.setHorizontalAlignment(SwingConstants.LEFT);
        wrongsField.setColumns(10);
        fieldsPanel.add(wrongsField);

        fieldsPanel.add(new JBLabel(I18nSupport.getValue("repository.view.field.errors.label")));

        JBTextField errorsField = new JBTextField("0");
        errorsField.setEditable(false);
        errorsField.setHorizontalAlignment(SwingConstants.LEFT);
        errorsField.setColumns(10);
        fieldsPanel.add(errorsField);

        mainPanel.add(fieldsPanel, BorderLayout.NORTH);

        tree = new SimpleTree();
        tree.setCellRenderer(new LDTreeCellRenderer());
        tree.setRootVisible(true);

        mainPanel.add(tree, BorderLayout.CENTER);


        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel,
                ldProject.getIdeaProject().getName() + " [" + ldProject.getSystemUnderTest().getName() + "]", false);
        content.setIcon(Icons.LIVINGDOC);
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);

        toolWindow.getContentManager().addContent(content);
    }

    private void loadRepositories() {

        LDNode ideaProjectNode = new LDNode(ldProject.getIdeaProject().getName(), Icons.PROJECT);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(ideaProjectNode);

        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<Repository> repositories = service.getAllRepositoriesForSystemUnderTest(ldProject.getSystemUnderTest(), ldProject.getIdentifier());
            for (Repository repository : repositories) {

                LDNode repositoryNode = new LDNode(repository.getProject().getName(), Icons.REPOSITORY);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(repositoryNode);
                rootNode.add(childNode);

                DocumentNode documentNode = service.getSpecificationHierarchy(repository, ldProject.getSystemUnderTest(), ldProject.getIdentifier());
                paintDocumentNode(documentNode.getChildren(), childNode);
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(ldse.getMessage(), I18nSupport.getValue("repository.view.error.loading.repositories"));
            LOG.error(ldse);
        }

        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode, true);
        tree.setModel(treeModel);
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
