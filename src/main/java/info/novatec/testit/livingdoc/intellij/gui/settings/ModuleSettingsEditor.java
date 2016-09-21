package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.core.ModuleSettings;
import info.novatec.testit.livingdoc.intellij.gui.UIUtils;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Graphical user interface for the module settings.<br>
 *
 * @see ModuleSettings
 */
public class ModuleSettingsEditor extends JPanel {

    private static final Logger LOG = Logger.getInstance(ModuleSettings.class);

    private final ModuleSettings moduleSettings;
    private final Project project;

    private JPanel myWholePanel;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JPanel mainPanel;

    private JBCheckBox livingDocEnabledCheck;
    private ComboBox<String> projectCombo;
    private ComboBox<String> sudCombo;
    private JBTextField userField;
    private JBPasswordField passField;
    private JBTextField classField;
    private JBTextField argsField;
    private JBLabel errorLabel;


    public ModuleSettingsEditor(final Module module) {

        super(new BorderLayout());
        add(myWholePanel, BorderLayout.CENTER);
        errorLabel.setForeground(Color.RED);

        project = module.getProject();
        moduleSettings = ModuleSettings.getInstance(module);

        centerPanel.setBorder(UIUtils.createTitledBorder(I18nSupport.getValue("module.settings.title")));
        southPanel.setBorder(UIUtils.createTitledBorder(I18nSupport.getValue("module.settings.sud.title")));

        projectCombo.addActionListener(actionEvent -> loadSud());
    }

    public void apply() {
        moduleSettings.setLivingDocEnabled(livingDocEnabledCheck.isSelected());
        moduleSettings.setProject((String) projectCombo.getSelectedItem());
        moduleSettings.setSud((String) sudCombo.getSelectedItem());
        moduleSettings.setUser(userField.getText());
        moduleSettings.setPassword(String.valueOf(passField.getPassword()));
        moduleSettings.setSudClassName(classField.getText());
        moduleSettings.setSudArgs(argsField.getText());
    }

    public boolean isModified() {

        enableOrDisablePanel();

        return moduleSettings.isLivingDocEnabled() != livingDocEnabledCheck.isSelected()
                || !StringUtils.equals(moduleSettings.getProject(), (String) projectCombo.getSelectedItem())
                || !StringUtils.equals(moduleSettings.getSud(), (String) sudCombo.getSelectedItem())
                || isModifiedCredentials()
                || isModifiedFactory();
    }

    public void reset() {

        boolean isLivingDocEnabled = moduleSettings.isLivingDocEnabled();
        livingDocEnabledCheck.setSelected(isLivingDocEnabled);

        loadProjects();

        userField.setText(moduleSettings.getUser());
        passField.setText(moduleSettings.getPassword());
        classField.setText(moduleSettings.getSudClassName());
        argsField.setText(moduleSettings.getSudArgs());
    }

    private boolean isModifiedCredentials() {
        return !StringUtils.equals(moduleSettings.getUser(), userField.getText())
                || !StringUtils.equals(moduleSettings.getPassword(), String.valueOf(passField.getPassword()));
    }

    private boolean isModifiedFactory() {
        return !StringUtils.equals(moduleSettings.getSudClassName(), String.valueOf(classField.getText()))
                || !StringUtils.equals(moduleSettings.getSudArgs(), String.valueOf(argsField.getText()));
    }

    private void loadProjects() {

        PluginLivingDocXmlRpcClient service = new PluginLivingDocXmlRpcClient(project);

        try {
            Set<info.novatec.testit.livingdoc.server.domain.Project> projects = service.getAllProjects();
            for (info.novatec.testit.livingdoc.server.domain.Project prj : projects) {
                projectCombo.addItem(prj.getName());
            }
            if (StringUtils.isNotBlank(moduleSettings.getProject())) {
                projectCombo.setSelectedItem(moduleSettings.getProject());

            } else {
                projectCombo.setSelectedIndex(0);
            }
        } catch (LivingDocServerException ldse) {
            LOG.warn(ldse);
            errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.project"));
            errorLabel.setIcon(AllIcons.General.Error);
        }
    }

    private void loadSud() {

        sudCombo.removeAllItems();

        String selectedProject = (String) projectCombo.getSelectedItem();
        if (StringUtils.isBlank(selectedProject)) {
            return;
        }

        try {
            PluginLivingDocXmlRpcClient service = new PluginLivingDocXmlRpcClient(project);
            Set<SystemUnderTest> systems = service.getSystemUnderTestsOfProject(selectedProject);
            for (SystemUnderTest system : systems) {
                sudCombo.addItem(system.getName());
            }
            if (StringUtils.isNotBlank(moduleSettings.getSud())) {
                sudCombo.setSelectedItem(moduleSettings.getSud());

            } else {
                sudCombo.setSelectedIndex(0);
            }
        } catch (LivingDocServerException ldse) {
            LOG.warn(ldse);
            errorLabel.setText(I18nSupport.getValue("module.settings.error.loading.systems"));
            errorLabel.setIcon(AllIcons.General.Error);
        }
    }

    private void enableOrDisablePanel() {

        boolean isEnable = livingDocEnabledCheck.isSelected();

        mainPanel.setEnabled(isEnable);

        Component[] components = centerPanel.getComponents();
        components = ArrayUtils.addAll(components, southPanel.getComponents());
        for (Component component : components) {
            component.setEnabled(isEnable);
        }
    }
}
