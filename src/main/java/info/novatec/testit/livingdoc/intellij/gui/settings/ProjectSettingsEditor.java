package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import info.novatec.testit.livingdoc.intellij.common.I18nSupport;
import info.novatec.testit.livingdoc.intellij.common.PluginProperties;
import info.novatec.testit.livingdoc.intellij.domain.ProjectSettings;
import info.novatec.testit.livingdoc.intellij.gui.GuiUtils;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Graphical user interface for the project settings.<br>
 *
 * @see ProjectSettings
 */

public class ProjectSettingsEditor extends SettingsEditor<ProjectSettings> {

    private static final Logger LOG = Logger.getInstance(ProjectSettingsEditor.class);

    private JPanel myWholePanel;
    private JPanel centerPanel;
    private JPanel northPanel;

    private JBTextField urlField;
    private JBTextField userField;
    private JBPasswordField passField;
    private JButton testButton;
    private JBLabel infoLabel;

    private final String defaultServer;


    public ProjectSettingsEditor(@NotNull final Project project) {

        super(project);
        add(myWholePanel, BorderLayout.CENTER);

        northPanel.setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("global.settings.title")));
        centerPanel.setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("global.settings.subtitle")));

        testButton.addActionListener(actionEvent -> testConnection());

        defaultServer = PluginProperties.getValue("livingdoc.url.default");
    }

    @Override
    public void apply(@NotNull final ProjectSettings projectSettings) {

        projectSettings.setUrlServer(
                StringUtils.defaultIfBlank(urlField.getText(), defaultServer));
        projectSettings.setUser(userField.getText());
        projectSettings.setPassword(String.valueOf(passField.getPassword()));
    }

    @Override
    public boolean isModified(@NotNull final ProjectSettings projectSettings) {

        enableOrDisableTestButton();

        boolean credentialsModified = !StringUtils.equals(projectSettings.getUser(), userField.getText())
                || !StringUtils.equals(projectSettings.getPassword(), String.valueOf(passField.getPassword()));

        return !StringUtils.equals(StringUtils.defaultString(projectSettings.getUrlServer(), ""), urlField.getText())
                || credentialsModified;
    }

    @Override
    public void reset(@NotNull final ProjectSettings projectSettings) {

        infoLabel.setText("");
        infoLabel.setIcon(null);

        urlField.setText(StringUtils.defaultIfBlank(projectSettings.getUrlServer(), defaultServer));
        userField.setText(projectSettings.getUser());
        passField.setText(projectSettings.getPassword());
    }

    private void enableOrDisableTestButton() {

        testButton.setEnabled(StringUtils.isNotBlank(urlField.getText()));

    }

    private void testConnection() {

        PluginLivingDocXmlRpcClient service = new PluginLivingDocXmlRpcClient(project);

        try {
            boolean testOk = service.testConnection(urlField.getText());

            infoLabel.setForeground(UIUtil.isUnderDarcula() ? Color.WHITE : Color.BLACK);

            if (testOk) {
                infoLabel.setIcon(AllIcons.General.Information);
                infoLabel.setText(I18nSupport.getValue("global.settings.button.test.ok"));

            } else {
                infoLabel.setIcon(AllIcons.General.Warning);
                infoLabel.setText(I18nSupport.getValue("global.settings.button.test.ko"));
            }
        } catch (LivingDocServerException ldse) {
            LOG.warn(ldse);
            infoLabel.setForeground(Color.RED);
            infoLabel.setIcon(AllIcons.General.Error);
            infoLabel.setText(ldse.getMessage());
        }
    }
}
