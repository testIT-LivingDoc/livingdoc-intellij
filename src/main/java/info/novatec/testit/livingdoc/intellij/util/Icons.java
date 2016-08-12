package info.novatec.testit.livingdoc.intellij.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public final class Icons {

    public static final Icon LIVINGDOC = IconLoader.getIcon("/icons/livingdoc_16.gif");
    public static final Icon REPOSITORY = IconLoader.getIcon("/icons/repository.png");
    public static final Icon EXECUTABLE = IconLoader.getIcon("/icons/executable.png");
    public static final Icon EXE_DIFF = IconLoader.getIcon("/icons/executable_diff.png");
    public static final Icon EXE_WORKING = IconLoader.getIcon("/icons/executable_working.png");
    public static final Icon NOT_EXECUTABLE = IconLoader.getIcon("/icons/notexecutable.png");
    public static final Icon ERROR = IconLoader.getIcon("/icons/error.png");

    private Icons() {
        // Utility class.
    }
}
