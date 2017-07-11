package org.livingdoc.intellij.common;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public final class Icons {

    public static final Icon LIVINGDOC = IconLoader.getIcon("/icons/livingdoc_16.gif");
    public static final Icon EXECUTABLE = IconLoader.getIcon("/icons/executable.png");
    public static final Icon EXE_DIFF = IconLoader.getIcon("/icons/executable_diff.png");
    public static final Icon EXE_WORKING = IconLoader.getIcon("/icons/executable_working.png");
    public static final Icon ERROR = IconLoader.getIcon("/icons/error.png");
    public static final Icon ERROR_DIFF = IconLoader.getIcon("/icons/error_diff.png");
    public static final Icon ERROR_WORKING = IconLoader.getIcon("/icons/error_working.png");
    public static final Icon SUCCESS = IconLoader.getIcon("/icons/success.png");
    public static final Icon SUCCESS_DIFF = IconLoader.getIcon("/icons/success_diff.png");
    public static final Icon SUCCESS_WORKING = IconLoader.getIcon("/icons/success_working.png");

    private Icons() {
        // Utility class.
    }
}
