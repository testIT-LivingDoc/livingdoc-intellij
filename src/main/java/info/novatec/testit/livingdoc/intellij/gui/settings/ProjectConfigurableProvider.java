package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public class ProjectConfigurableProvider extends ConfigurableProvider {

    private final Project project;


    public ProjectConfigurableProvider(final Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new ProjectConfigurableImpl(project);
    }
}
