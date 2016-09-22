package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import info.novatec.testit.livingdoc.intellij.core.ModuleSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Controller for {@link ModuleSettingsEditor}
 *
 * @see Configurable
 * @see ModuleSettingsEditor
 */
public class ModuleConfigurableImpl extends AbstractConfigurableImpl<ModuleSettings> {

    public ModuleConfigurableImpl(@NotNull final Module module) {
        super(module.getProject(), ModuleSettings.getInstance(module));
    }

    @Override
    public SettingsEditor createSettingsEditor() {
        return new ModuleSettingsEditor(project);
    }

}
