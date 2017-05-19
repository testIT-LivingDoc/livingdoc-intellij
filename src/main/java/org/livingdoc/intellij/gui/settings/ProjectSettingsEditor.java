package org.livingdoc.intellij.gui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.content.Content;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.common.PluginProperties;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.LivingDocException;
import org.livingdoc.intellij.domain.ProjectSettings;
import org.livingdoc.intellij.gui.GuiUtils;
import org.livingdoc.intellij.gui.toolwindows.ToolWindowPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Graphical user interface for the project settings.<br>
 *
 * @see ProjectSettings
 */

public class ProjectSettingsEditor extends SettingsEditor<ProjectSettings> {

    private static final Logger LOG = Logger.getInstance(ProjectSettingsEditor.class);
    private final String defaultServer;
    private JPanel myWholePanel;
    private JPanel centerPanel;
    private JPanel northPanel;
    private JBTextField urlField;
    private JBTextField userField;
    private JBPasswordField passField;
    private JButton testButton;
    private JBLabel infoLabel;

    public ProjectSettingsEditor(@NotNull final Project project) {

        super(project);
        add(myWholePanel, BorderLayout.CENTER);

        northPanel.setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("global.settings.title")));
        centerPanel.setBorder(GuiUtils.createTitledBorder(I18nSupport.getValue("global.settings.subtitle")));

        testButton.addActionListener(actionEvent -> testConnectionAction());

        defaultServer = PluginProperties.getValue("livingdoc.url.default");
    }

    @Override
    public void apply(@NotNull final ProjectSettings projectSettings) {

        applyChanges(projectSettings);

        try {
            projectSettings.setConnected(testConnection(projectSettings));
        } catch (LivingDocException lde) {
            LOG.warn(lde);
        }

        refreshToolWindows();
    }

    private void refreshToolWindows() {

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PluginProperties.getValue("toolwindows.id"));
        toolWindow.activate(null);

        for (Content content : toolWindow.getContentManager().getContents()) {
            if (content.getComponent() instanceof ToolWindowPanel) {
                ToolWindowPanel toolWindowPanel = (ToolWindowPanel) content.getComponent();
                toolWindowPanel.getRefreshAction().actionPerformed(null);
            }
        }
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

    private void applyChanges(@NotNull ProjectSettings projectSettings) {
        projectSettings.setUrlServer(
                StringUtils.defaultIfBlank(urlField.getText(), defaultServer));
        projectSettings.setUser(userField.getText());
        projectSettings.setPassword(String.valueOf(passField.getPassword()));
    }

    private boolean testConnection(@NotNull ProjectSettings projectSettings) throws LivingDocException {
        // To save changes is better delegating in the IDE (when the user clicks on the Apply/OK buttons)
        // so we are using a temporal ProjectSettings to test the connection
        LivingDocConnector livingDocConnector = LivingDocConnector.newInstance(projectSettings);

        boolean result = false;

        if (StringUtils.isNoneBlank(projectSettings.getPassword(), projectSettings.getUser(), projectSettings.getUrlServer())) {
            result = livingDocConnector.testConnection();
        }
        return result;
    }

    private void testConnectionAction() {
        try {
            infoLabel.setForeground(UIUtil.isUnderDarcula() ? Color.WHITE : Color.BLACK);

            if (testConnection(getTemporalProjectSetting())) {
                infoLabel.setIcon(AllIcons.General.Information);
                infoLabel.setText(I18nSupport.getValue("global.settings.button.test.ok"));

            } else {
                infoLabel.setIcon(AllIcons.General.Warning);
                infoLabel.setText(I18nSupport.getValue("global.settings.button.test.ko"));
            }
        } catch (LivingDocException lde) {
            LOG.warn(lde);
            infoLabel.setForeground(Color.RED);
            infoLabel.setIcon(AllIcons.General.Error);
            infoLabel.setText(lde.getMessage());
        }
    }

    private ProjectSettings getTemporalProjectSetting() {
        ProjectSettings projectSettings = new ProjectSettings();
        applyChanges(projectSettings);
        return projectSettings;
    }
}
