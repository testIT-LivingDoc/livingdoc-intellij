package info.novatec.testit.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.swing.*;
import java.io.Serializable;

/**
 * Parent class for the nodes of the repository view tree
 * @see LDNodeType
 */
public class LDNode implements Serializable {

    private static final long serialVersionUID = 4522875652776261867L;

    private String name;
    private Icon icon;
    private LDNodeType type;

    LDNode(final String nodeName, final Icon nodeIcon, final LDNodeType nodeType) {
        this.name = nodeName;
        this.icon = nodeIcon;
        this.type = nodeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("icon", icon)
                .append("type", type)
                .toString();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(final Icon icon) {
        this.icon = icon;
    }

    public LDNodeType getType() {
        return type;
    }

    public void setType(final LDNodeType type) {
        this.type = type;
    }
}
