package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import info.novatec.testit.livingdoc.intellij.core.ProjectSettings;
import info.novatec.testit.livingdoc.intellij.gui.UIUtils;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Graphical user interface for the project settings.<br>
 *
 * @see ProjectSettings
 */

public class ProjectSettingsEditor extends JPanel {

    private static final Logger LOG = Logger.getInstance(ProjectSettingsEditor.class);


    private final ProjectSettings projectSettings;
    private final Project project;

    private JPanel myWholePanel;

    private JBTextField urlTextField;
    private JBTextField handlerTextField;

    private JButton testButton;
    private JBLabel infoLabel;


    public ProjectSettingsEditor(final Project project) {

        super(new BorderLayout());


        setBorder(UIUtils.createTitledBorder(I18nSupport.getValue("server.configuration.title")));

        this.project = project;
        this.projectSettings = ProjectSettings.getInstance(project);

        testButton.addActionListener(actionEvent -> testConnection());

        this.add(myWholePanel, BorderLayout.CENTER);

    }

    private void testConnection() {

        RpcClientService service = new PluginLivingDocXmlRpcClient(project);

        try {
            boolean testOk = service.testConnection(urlTextField.getText(), handlerTextField.getText());
            infoLabel.setForeground(UIUtil.isUnderDarcula() ? Color.WHITE : Color.BLACK);
            if (testOk) {
                infoLabel.setIcon(AllIcons.General.Information);
                infoLabel.setText(I18nSupport.getValue("server.configuration.button.test.ok"));

            } else {
                infoLabel.setIcon(AllIcons.General.Warning);
                infoLabel.setText(I18nSupport.getValue("server.configuration.button.test.ko"));
            }
        } catch (LivingDocServerException ldse) {
            LOG.warn(ldse);
            infoLabel.setForeground(Color.RED);
            infoLabel.setIcon(AllIcons.General.Error);
            infoLabel.setText(ldse.getMessage());
        }
    }

    public void apply() {
        projectSettings.setHandler(handlerTextField.getText());
        projectSettings.setUrlServer(urlTextField.getText());
    }

    public boolean isModified() {

        enableOrDisableTestButton();

        return !StringUtils.equals(StringUtils.defaultString(projectSettings.getHandler(), ""), handlerTextField.getText())
                || !StringUtils.equals(StringUtils.defaultString(projectSettings.getUrlServer(), ""), urlTextField.getText());
    }

    public void reset() {
        urlTextField.setText(projectSettings.getUrlServer());
        handlerTextField.setText(projectSettings.getHandler());
        infoLabel.setText("");
        infoLabel.setIcon(null);

    }

    private void enableOrDisableTestButton() {

        testButton.setEnabled(StringUtils.isNoneBlank(handlerTextField.getText(), urlTextField.getText()));
    }
}
