package org.livingdoc.intellij.core;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.livingdoc.intellij.gui.settings.ProjectConfigurableImpl;

/**
 * Provider class for project configurable extension defined in <b>plugin.xml</b> with
 * <code>id="LivingDoc.Project.Configurable"</code>.<br>
 * (File > Settings > LivingDoc)
 *
 * @see ConfigurableProvider
 * @see ProjectConfigurableImpl
 */
public class ProjectConfigurableProvider extends ConfigurableProvider {

    private final Project project;

    public ProjectConfigurableProvider(@NotNull final Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new ProjectConfigurableImpl(project);
    }
}
