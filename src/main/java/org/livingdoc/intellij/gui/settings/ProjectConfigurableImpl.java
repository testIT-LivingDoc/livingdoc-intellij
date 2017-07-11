package org.livingdoc.intellij.gui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.livingdoc.intellij.domain.ProjectSettings;

/**
 * Controller for {@link ProjectSettingsEditor}
 *
 * @see Configurable
 * @see ProjectSettingsEditor
 */
public class ProjectConfigurableImpl extends AbstractConfigurableImpl<ProjectSettings> {

    public ProjectConfigurableImpl(final Project project) {
        super(project, ProjectSettings.getInstance(project));
    }

    @Override
    public SettingsEditor createSettingsEditor() {
        return new ProjectSettingsEditor(project);
    }

}
