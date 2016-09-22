package info.novatec.testit.livingdoc.intellij.gui.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public abstract class SettingsEditor<V> extends JPanel {

    final transient Project project;


    SettingsEditor(@NotNull final Project project) {

        super(new BorderLayout());

        this.project = project;
    }

    public abstract void apply(V settings);

    public abstract boolean isModified(V settings);

    public abstract void reset(V settings);

}
