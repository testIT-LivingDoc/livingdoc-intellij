package org.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.NodeType;

import javax.swing.*;
import java.io.Serializable;

/**
 * Parent class for the nodes of the repository view tree
 *
 * @see NodeType
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 4522875652776261867L;
    private NodeType type;
    private Node parent;
    private String nodeName;
    private transient Icon icon;

    public Node() {
        parent = null;
        type = null;
    }

    public Node(final String nodeName, final Icon nodeIcon, final NodeType nodeType, final Node parent) {
        this(nodeName, nodeIcon, nodeType);
        this.parent = parent;
    }

    public Node(final String nodeName, final Icon nodeIcon, final NodeType nodeType) {
        this.nodeName = nodeName;
        this.icon = nodeIcon;
        this.type = nodeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("parent", parent)
                .append("nodeName", nodeName)
                .append("icon", icon)
                .toString();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
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

    public void setType(final NodeType type) {
        this.type = type;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(final Node parent) {
        this.parent = parent;
    }
}
