package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * The method {@link #createTemplateConfiguration(Project)} is called once per project to create the template run
 * configuration; and all real run configurations (loaded from the workspace or created by the user) are called by
 * cloning the template through the {{@link #createConfiguration(String, RunConfiguration)}} method.
 *
 * @see ConfigurationFactory
 * @see RemoteRunConfiguration
 */
public class RemoteConfigurationFactory extends ConfigurationFactory {

    protected RemoteConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @Override
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new RemoteRunConfiguration(project, this, getType().getDisplayName());
    }
}
