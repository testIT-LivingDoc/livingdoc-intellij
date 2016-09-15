package info.novatec.testit.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.swing.*;
import java.io.Serializable;

/**
 * Parent class for the nodes of the repository view tree
 *
 * @see NodeType
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 4522875652776261867L;

    private final String name;
    private final NodeType type;
    private final Node parent;
    private Icon icon;

    public Node(final String nodeName, final Icon nodeIcon, final NodeType nodeType, final Node parent) {
        this.name = nodeName;
        this.icon = nodeIcon;
        this.type = nodeType;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("icon", icon)
                .append("type", type)
                .append("parent", parent)
                .toString();
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(final Icon icon) {
        this.icon = icon;
    }

    public NodeType getType() {
        return type;
    }

    public Node getParent() {
        return parent;
    }
}
