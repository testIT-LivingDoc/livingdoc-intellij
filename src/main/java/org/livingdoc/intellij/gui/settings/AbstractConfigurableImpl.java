package org.livingdoc.intellij.gui.settings;


import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.livingdoc.intellij.common.I18nSupport;

import javax.swing.*;

abstract class AbstractConfigurableImpl<V> implements Configurable {

    final Project project;

    private final V settings;

    private SettingsEditor settingsEditor;


    AbstractConfigurableImpl(@NotNull final Project project, @NotNull final V settings) {
        this.project = project;
        this.settings = settings;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return I18nSupport.getValue("module.settings.tab.title");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsEditor == null) {
            settingsEditor = createSettingsEditor();
        }
        return settingsEditor;
    }

    @Override
    public boolean isModified() {
        return settingsEditor.isModified(settings);
    }

    @Override
    public void apply() throws ConfigurationException {
        settingsEditor.apply(settings);
    }

    @Override
    public void reset() {
        settingsEditor.reset(settings);
    }

    @Override
    public void disposeUIResources() {
        // Nothing to do
    }

    @NotNull
    protected abstract SettingsEditor createSettingsEditor();
}
