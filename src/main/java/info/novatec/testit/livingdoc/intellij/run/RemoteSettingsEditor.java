package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.impl.CheckableRunConfigurationEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;

import javax.swing.*;
import java.awt.*;

/**
 * TO specify execution options of LivingDoc's Plugin
 * @see RemoteRunConfiguration
 * @see CheckableRunConfigurationEditor
 */
public class RemoteSettingsEditor extends SettingsEditor<RemoteRunConfiguration> implements CheckableRunConfigurationEditor {

    private final Project ideaProject;

    public RemoteSettingsEditor(Project project) {
        ideaProject = project;
    }

    @Override
    protected void resetEditorFrom(RemoteRunConfiguration runConfiguration) {
        //Messages.showInfoMessage(runConfiguration.toString(),"resetEditorFrom()");
    }

    @Override
    protected void applyEditorTo(RemoteRunConfiguration runConfiguration) throws ConfigurationException {
        //Messages.showInfoMessage(runConfiguration.toString(),"applyEditorTo()");
    }

    @Override
    protected JComponent createEditor() {

        JPanel jPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 2, 5, 0);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.project.label")),constraints);

        ModulesComboBox modulesComboBox = new ModulesComboBox();
        modulesComboBox.fillModules(ideaProject);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        jPanel.add(modulesComboBox, constraints);
        constraints.weightx = 0;

        constraints.gridx = 0;
        constraints.gridy = 1;
        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.repositoryuid.label")), constraints);

        JBTextField repositoryUIDField = new JBTextField();
        constraints.gridx = 1;
        jPanel.add(repositoryUIDField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.repositoryurl.label") + " "), constraints);
        JBTextField respositoryURLField = new JBTextField();
        constraints.gridx = 1;
        jPanel.add(respositoryURLField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.repositoryclass.label")), constraints);
        JBTextField respositoryClassField = new JBTextField();
        constraints.gridx = 1;
        jPanel.add(respositoryClassField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.username.label")), constraints);
        JBTextField usernameField = new JBTextField();
        constraints.gridx = 1;
        jPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.password.label")), constraints);
        JPasswordField passwordField = new JPasswordField();
        constraints.gridx = 1;
        jPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        jPanel.add(new JBLabel(I18nSupport.getValue("run.configuration.field.specifications.label")), constraints);
        JBTextField specificationsField = new JBTextField();
        constraints.gridx = 1;
        jPanel.add(specificationsField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 7;
        JBCheckBox workingCheckBox = new JBCheckBox();
        workingCheckBox.setText(I18nSupport.getValue("run.configuration.field.working.label"));
        jPanel.add(workingCheckBox, constraints);

        return jPanel;
    }

    @Override
    public void checkEditorData(Object s) {
        //Messages.showInfoMessage(s.getClass().getName(),"checkEditorData()");
    }
}
