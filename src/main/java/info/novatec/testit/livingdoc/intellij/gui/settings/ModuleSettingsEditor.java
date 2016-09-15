package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.gui.UIUtils;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
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

    private final static Logger LOG = Logger.getInstance(ModuleSettings.class);

    private final Project project;
    private final ModuleSettings moduleSettings;

    private JPanel myWholePanel;
    private JPanel centerPanel;

    private JBCheckBox livingDocEnabledCheck;
    private ComboBox projectCombo;
    private ComboBox sudCombo;
    private JBTextField userField;
    private JBPasswordField passField;


    public ModuleSettingsEditor(final Module module) {

        super(new BorderLayout());

        this.project = module.getProject();
        this.moduleSettings = ModuleSettings.getInstance(module);

        centerPanel.setBorder(UIUtils.createTitledBorder(I18nSupport.getValue("identify.project.title")));

        livingDocEnabledCheck.addActionListener(actionEvent -> enableOrDisablePanel());
        projectCombo.addActionListener(actionEvent -> loadSud());

        this.add(myWholePanel, BorderLayout.CENTER);
    }

    public void apply() {
        moduleSettings.setLivingDocEnabled(livingDocEnabledCheck.isSelected());
        moduleSettings.setProject((String) projectCombo.getSelectedItem());
        moduleSettings.setSud((String) sudCombo.getSelectedItem());
        moduleSettings.setUser(userField.getText());
        moduleSettings.setPassword(String.valueOf(passField.getPassword()));
    }

    public boolean isModified() {
        return moduleSettings.isLivingDocEnabled() != livingDocEnabledCheck.isSelected()
                || !StringUtils.equals(moduleSettings.getProject(), (String) projectCombo.getSelectedItem())
                || !StringUtils.equals(moduleSettings.getSud(), (String) sudCombo.getSelectedItem())
                || !StringUtils.equals(moduleSettings.getUser(), userField.getText())
                || !StringUtils.equals(moduleSettings.getPassword(), String.valueOf(passField.getPassword()));
    }

    public void reset() {

        boolean isLivingDocEnabled = moduleSettings.isLivingDocEnabled();
        livingDocEnabledCheck.setSelected(isLivingDocEnabled);
        enableOrDisablePanel();

        if (isLivingDocEnabled) {
            loadProjects();
        }
        userField.setText(moduleSettings.getUser());
        passField.setText(moduleSettings.getPassword());
    }

    private void loadProjects() {
        PluginLivingDocXmlRpcClient service = new PluginLivingDocXmlRpcClient();
        try {
            Set<info.novatec.testit.livingdoc.server.domain.Project> projects = service.getAllProjects();
            for (info.novatec.testit.livingdoc.server.domain.Project project : projects) {
                projectCombo.addItem(project.getName());
            }
            if (StringUtils.isNotBlank(moduleSettings.getProject())) {
                projectCombo.setSelectedItem(moduleSettings.getProject());
            } else {
                projectCombo.setSelectedIndex(0);
            }
        } catch (LivingDocServerException ldse) {
            // TODO label with the error:
            Messages.showErrorDialog(project, I18nSupport.getValue("identify.project.error.loading.project.desc"),
                    I18nSupport.getValue("identify.project.error.loading.project"));
            LOG.error(ldse);
        }
    }

    private void loadSud() {

        sudCombo.removeAllItems();

        String selectedProject = (String) projectCombo.getSelectedItem();
        if (StringUtils.isBlank(selectedProject)) {
            return;
        }

        try {
            PluginLivingDocXmlRpcClient service = new PluginLivingDocXmlRpcClient();
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
            Messages.showErrorDialog(ldse.getMessage(), I18nSupport.getValue("identify.project.error.loading.systems"));
            LOG.error(ldse);
        }
    }

    private void enableOrDisablePanel() {
        boolean isEnable = livingDocEnabledCheck.isSelected();
        centerPanel.setEnabled(isEnable);
        for (Component component : centerPanel.getComponents()) {
            component.setEnabled(isEnable);
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof ComboBox) {
                ((ComboBox) component).setSelectedIndex(-1);
            }
        }
        if (isEnable) {
            loadProjects();
        }
    }
}
