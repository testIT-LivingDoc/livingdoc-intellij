package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Controller for {@link ModuleSettingsEditor}
 *
 * @see Configurable
 * @see ModuleSettingsEditor
 */
public class ModuleConfigurableImpl implements Configurable {

    @NotNull
    private final Module module;
    private ModuleSettingsEditor moduleSettingsEditor;

    public ModuleConfigurableImpl(@NotNull final Module module) {
        this.module = module;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return I18nSupport.getValue("settings.module.title");
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
        if (moduleSettingsEditor == null) {
            moduleSettingsEditor = new ModuleSettingsEditor(module);
        }
        return moduleSettingsEditor;
    }

    @Override
    public boolean isModified() {
        return moduleSettingsEditor.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        moduleSettingsEditor.apply();
    }

    @Override
    public void reset() {
        moduleSettingsEditor.reset();
    }

    @Override
    public void disposeUIResources() {
        // Nothing to do
    }
}
