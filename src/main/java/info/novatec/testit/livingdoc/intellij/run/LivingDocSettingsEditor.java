package info.novatec.testit.livingdoc.intellij.run;

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
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiMethodUtil;
import com.intellij.ui.EditorTextFieldWithBrowseButton;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * TO specify execution options of LivingDoc's Plugin
 * The UI is defined in LivingDocSettingsEditor.form
 *
 * @see RemoteRunConfiguration
 * @see CheckableRunConfigurationEditor
 */
public class LivingDocSettingsEditor extends SettingsEditor<RemoteRunConfiguration> implements PanelWithAnchor {

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


    public LivingDocSettingsEditor(Project project) {

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
        workingVersionCheckBox.getComponent().setSelected(remoteRunConfiguration.isWorkingVersion());
    }

    @Override
    protected void applyEditorTo(@NotNull RemoteRunConfiguration remoteRunConfiguration) throws ConfigurationException {

        myModuleSelector.applyTo(remoteRunConfiguration);
        myCommonProgramParameters.applyTo(remoteRunConfiguration);
        Module selectedModule = (Module) myModule.getComponent().getSelectedItem();
        remoteRunConfiguration.setModule(selectedModule);

        remoteRunConfiguration.MAIN_CLASS_NAME = myMainClass.getComponent().getText();

        remoteRunConfiguration.setRepositoryUID(repositoryUIDField.getText());
        remoteRunConfiguration.setRepositoryURL(repositoryURLField.getText());
        remoteRunConfiguration.setRepositoryClass(repositoryClassField.getText());
        remoteRunConfiguration.setSpecificationName(specificationsField.getText());
        remoteRunConfiguration.setWorkingVersion(workingVersionCheckBox.getComponent().isSelected());
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
        myMainClass.setComponent(new EditorTextFieldWithBrowseButton(myProject, true, new JavaCodeFragment.VisibilityChecker() {
            @Override
            public Visibility isDeclarationVisible(PsiElement declaration, PsiElement place) {
                if (declaration instanceof PsiClass) {
                    final PsiClass aClass = (PsiClass) declaration;
                    if (ConfigurationUtil.MAIN_CLASS.value(aClass) && PsiMethodUtil.findMainMethod(aClass) != null) {
                        return Visibility.VISIBLE;
                    }
                }
                return JavaCodeFragment.VisibilityChecker.Visibility.NOT_VISIBLE;
            }
        }));
    }
}
