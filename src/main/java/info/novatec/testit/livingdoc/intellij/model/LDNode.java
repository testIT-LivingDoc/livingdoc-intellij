package info.novatec.testit.livingdoc.intellij.model;

import info.novatec.testit.livingdoc.server.domain.DocumentNode;

import javax.swing.*;
import java.io.Serializable;

/**
 * Custom Tree Node for LivingDoc plugin.
 * It's the user object for {@link javax.swing.tree.DefaultMutableTreeNode}
 * Built from {@link info.novatec.testit.livingdoc.server.domain.DocumentNode}
 */
public class LDNode implements Serializable {

    private static final long serialVersionUID = 4522875652776261867L;

    private String name;
    private Icon icon;
    private boolean executable = false;
    private boolean canBeImplemented = false;

    public LDNode(final String name, final Icon icon) {
        this.name = name;
        this.icon = icon;
    }

    public LDNode(DocumentNode child, final Icon icon) {
        this(child.getTitle(), icon);
        this.executable = child.isExecutable();
        this.canBeImplemented = child.canBeImplemented();
    }

    public boolean canBeImplemented() {
        return canBeImplemented;
    }

    public void setCanBeImplemented(boolean canBeImplemented) {
        this.canBeImplemented = canBeImplemented;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
