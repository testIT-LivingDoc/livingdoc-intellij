package info.novatec.testit.livingdoc.intellij.util;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface Icons {

    Icon LIVINGDOC = IconLoader.getIcon("/icons/livingdoc_16.gif");
    Icon PROJECT = IconLoader.getIcon("/icons/project.gif");
    Icon REPOSITORY = IconLoader.getIcon("/icons/repository.png");
    Icon EXECUTABLE = IconLoader.getIcon("/icons/executable.png");
    Icon EXE_DIFF = IconLoader.getIcon("/icons/executable_diff.png");
    Icon EXE_WORKING = IconLoader.getIcon("/icons/executable_working.png");
    Icon NOT_EXECUTABLE = IconLoader.getIcon("/icons/notexecutable.png");
    Icon ERROR = IconLoader.getIcon("/icons/error.png");
    Icon FLAG_RIGHT = IconLoader.getIcon("/icons/flag_green.gif");
    Icon FLAG_WRONG = IconLoader.getIcon("/icons/flag_orange.gif");
    Icon FLAG_ERROR = IconLoader.getIcon("/icons/flag_red.gif");

}
