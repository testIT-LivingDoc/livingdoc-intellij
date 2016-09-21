package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


/**
 * Controller for {@link ProjectSettingsEditor}
 *
 * @see Configurable
 * @see ProjectSettingsEditor
 */
public class ProjectConfigurableImpl implements Configurable {


    private final Project project;
    private ProjectSettingsEditor projectSettingsEditor;

    public ProjectConfigurableImpl(final Project project) {
        this.project = project;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return I18nSupport.getValue("server.configuration.title");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        // TODO return the official doc url of LivingDoc
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (projectSettingsEditor == null) {
            projectSettingsEditor = new ProjectSettingsEditor(project);
        }
        return projectSettingsEditor;
    }

    @Override
    public boolean isModified() {

        return projectSettingsEditor.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        projectSettingsEditor.apply();
    }

    @Override
    public void reset() {
        projectSettingsEditor.reset();
    }

    @Override
    public void disposeUIResources() {
        // Nothing to do
    }


}
