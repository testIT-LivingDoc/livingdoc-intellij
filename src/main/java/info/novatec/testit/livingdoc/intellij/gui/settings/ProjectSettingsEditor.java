package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import info.novatec.testit.livingdoc.intellij.common.I18nSupport;
import info.novatec.testit.livingdoc.intellij.common.PluginProperties;
import info.novatec.testit.livingdoc.intellij.domain.ProjectSettings;
import info.novatec.testit.livingdoc.intellij.gui.GuiUtils;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
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


    private JBTextField urlTextField;
    private JBTextField handlerTextField;

    private JButton testButton;
    private JBLabel infoLabel;


    public ProjectSettingsEditor(@NotNull final Project project) {

        super(project);
        add(myWholePanel, BorderLayout.CENTER);

        setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("global.settings.title")));

        testButton.addActionListener(actionEvent -> testConnection());
    }

    @Override
    public void apply(@NotNull final ProjectSettings projectSettings) {

        projectSettings.setUrlServer(
                StringUtils.defaultIfBlank(urlTextField.getText(), PluginProperties.getValue("livingdoc.url.default")));
        projectSettings.setHandler(
                StringUtils.defaultIfBlank(handlerTextField.getText(), PluginProperties.getValue("livingdoc.handler.default")));
    }

    @Override
    public boolean isModified(@NotNull final ProjectSettings projectSettings) {

        enableOrDisableTestButton();

        return !StringUtils.equals(StringUtils.defaultString(projectSettings.getHandler(), ""), handlerTextField.getText())
                || !StringUtils.equals(StringUtils.defaultString(projectSettings.getUrlServer(), ""), urlTextField.getText());
    }

    @Override
    public void reset(@NotNull final ProjectSettings projectSettings) {
        urlTextField.setText(projectSettings.getUrlServer());
        handlerTextField.setText(projectSettings.getHandler());
        infoLabel.setText("");
        infoLabel.setIcon(null);
    }

    private void enableOrDisableTestButton() {

        testButton.setEnabled(StringUtils.isNoneBlank(handlerTextField.getText(), urlTextField.getText()));
    }

    private void testConnection() {

        RpcClientService service = new PluginLivingDocXmlRpcClient(project);

        try {
            boolean testOk = service.testConnection(urlTextField.getText(), handlerTextField.getText());

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
