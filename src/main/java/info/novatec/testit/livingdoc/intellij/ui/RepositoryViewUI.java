package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Factory class for tool window configured in plugin.xml
 * // TODO complete javadoc
 * @see ToolWindowFactory
 */
public class RepositoryViewUI implements ToolWindowFactory {

    private JPanel jPanel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        jPanel = new JPanel();
        addComponents();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jPanel, "xdisplayName", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void addComponents() {

        jPanel.add(new JBLabel("xRights:"));
        JBTextField rightsField = new JBTextField("0");
        rightsField.setEditable(false);
        rightsField.setHorizontalAlignment(SwingConstants.LEFT);
        rightsField.setColumns(10);
        jPanel.add(rightsField);

        jPanel.add(new JBLabel("xWrongs:"));
        JBTextField wrongsField = new JBTextField("0");
        wrongsField.setEditable(false);
        wrongsField.setHorizontalAlignment(SwingConstants.LEFT);
        wrongsField.setColumns(10);
        jPanel.add(wrongsField);

        jPanel.add(new JBLabel("xErrors:"));
        JBTextField errorsField = new JBTextField("0");
        errorsField.setEditable(false);
        errorsField.setHorizontalAlignment(SwingConstants.LEFT);
        errorsField.setColumns(10);
        jPanel.add(errorsField);

        // TODO Continue with Tree table, getAllRepositoriesForSystemUnderTest, getSpecificationHierarchy
    }
}
