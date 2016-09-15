package info.novatec.testit.livingdoc.intellij.gui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.configurations.ConfigurationUtil;
import com.intellij.execution.impl.CheckableRunConfigurationEditor;
import com.intellij.execution.ui.ClassBrowser;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.ConfigurationModuleSelector;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.JavaCodeFragment;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import info.novatec.testit.livingdoc.intellij.run.RemoteRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * To specify execution options of LivingDoc's Plugin
 * The UI is defined in RunConfigurationEditorform
 *
 * @see RemoteRunConfiguration
 * @see CheckableRunConfigurationEditor
 */
public class RunConfigurationEditor extends SettingsEditor<RemoteRunConfiguration> implements PanelWithAnchor {

    private final Project myProject;
    private final ConfigurationModuleSelector myModuleSelector;
    private JPanel myWholePanel;
    private JComponent myAnchor;
    private CommonJavaParametersPanel myCommonProgramParameters;
    private LabeledComponent<ModulesComboBox> myModule;
    private LabeledComponent<JBTextField> repositoryUIDField;
    private LabeledComponent<JBTextField> repositoryURLField;
    private LabeledComponent<JBTextField> repositoryClassField;
    private LabeledComponent<JBTextField> specificationsField;
    private LabeledComponent<JBCheckBox> workingVersionCheckBox;
    private LabeledComponent<EditorTextFieldWithBrowseButton> myMainClass;


    public RunConfigurationEditor(Project project) {

        myProject = project;

        myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());

        ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(myMainClass.getComponent());

        myAnchor = UIUtil.mergeComponentsWithAnchor(repositoryUIDField, repositoryURLField, repositoryClassField,
                specificationsField, workingVersionCheckBox, myMainClass, myModule, myCommonProgramParameters);
    }

    @Override
    protected void resetEditorFrom(@NotNull RemoteRunConfiguration remoteRunConfiguration) {

        myModuleSelector.reset(remoteRunConfiguration);
        myCommonProgramParameters.reset(remoteRunConfiguration);

        myMainClass.getComponent().setText(remoteRunConfiguration.MAIN_CLASS_NAME);

        repositoryUIDField.getComponent().setText(remoteRunConfiguration.getRepositoryUID());
        repositoryURLField.getComponent().setText(remoteRunConfiguration.getRepositoryURL());
        repositoryClassField.getComponent().setText(remoteRunConfiguration.getRepositoryClass());
        specificationsField.getComponent().setText(remoteRunConfiguration.getSpecificationName());
        workingVersionCheckBox.getComponent().setSelected(remoteRunConfiguration.isCurrentVersion());
    }

    @Override
    protected void applyEditorTo(@NotNull RemoteRunConfiguration remoteRunConfiguration) throws ConfigurationException {

        myModuleSelector.applyTo(remoteRunConfiguration);
        myCommonProgramParameters.applyTo(remoteRunConfiguration);
        Module selectedModule = (Module) myModule.getComponent().getSelectedItem();
        remoteRunConfiguration.setModule(selectedModule);

        remoteRunConfiguration.MAIN_CLASS_NAME = myMainClass.getComponent().getText();

        remoteRunConfiguration.setRepositoryUID(repositoryUIDField.getComponent().getText());
        remoteRunConfiguration.setRepositoryURL(repositoryURLField.getComponent().getText());
        remoteRunConfiguration.setRepositoryClass(repositoryClassField.getComponent().getText());
        remoteRunConfiguration.setSpecificationName(specificationsField.getComponent().getText());
        remoteRunConfiguration.setCurrentVersion(workingVersionCheckBox.getComponent().isSelected());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myWholePanel;
    }

    @Override
    public JComponent getAnchor() {
        return myAnchor;
    }

    @Override
    public void setAnchor(@Nullable JComponent anchor) {
        myAnchor = anchor;
        myCommonProgramParameters.setAnchor(anchor);
        myModule.setAnchor(anchor);
        myMainClass.setAnchor(anchor);
        repositoryUIDField.setAnchor(anchor);
        repositoryURLField.setAnchor(anchor);
        repositoryClassField.setAnchor(anchor);
        specificationsField.setAnchor(anchor);
        workingVersionCheckBox.setAnchor(anchor);
    }

    private void createUIComponents() {
        myMainClass = new LabeledComponent<>();
        myMainClass.setComponent(new EditorTextFieldWithBrowseButton(myProject, true, (declaration, place) -> {

            if (declaration instanceof PsiClass) {
                final PsiClass aClass = (PsiClass) declaration;
                if (ConfigurationUtil.MAIN_CLASS.value(aClass) && PsiMethodUtil.findMainMethod(aClass) != null) {
                    return JavaCodeFragment.VisibilityChecker.Visibility.VISIBLE;
                }
            }
            return JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE;
        }
        ));
    }
}
