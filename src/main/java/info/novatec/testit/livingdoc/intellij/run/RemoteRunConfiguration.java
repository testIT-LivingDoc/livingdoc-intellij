package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A named run configuration which can be executed. <br>
 * The {@link #getConfigurationEditor()} method returns the settings editor component (user interface) for the run
 * configuration settings (Displayed in multiple tabs if there's more than one):
 * <ul>
 *     <li>{@link RemoteSettingsEditor}</li>
 * </ul>
 * @see RunConfigurationBase
 * @see RunConfiguration
 */
public class RemoteRunConfiguration extends RunConfigurationBase {

    protected RemoteRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, final String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<RemoteRunConfiguration> settingsEditorGroup = new SettingsEditorGroup<>();
        RemoteSettingsEditor settingsEditor = new RemoteSettingsEditor(getProject());
        settingsEditorGroup.addEditor("Main", settingsEditor);
        return settingsEditorGroup;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        // TODO Checks whether the run configuration settings are valid.
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        // TODO for executing from Repository View
        return null;
    }
}
