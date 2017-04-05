package org.livingdoc.intellij.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.Icons;
import org.livingdoc.intellij.common.NodeType;

/**
 * Custom tree node for LivingDoc plugin.
 * It's the user object for {@link javax.swing.tree.DefaultMutableTreeNode}
 *
 * @see Node
 */
public class SpecificationNode extends Node {

    private boolean executable;
    private boolean canBeImplemented;
    private boolean usingCurrentVersion;
    

    public SpecificationNode(final String speficicationName, final Node parentNode) {

        super(speficicationName, Icons.EXECUTABLE, NodeType.SPECIFICATION, parentNode);

        this.usingCurrentVersion = false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("executable", executable)
                .append("canBeImplemented", canBeImplemented)
                .append("usingCurrentVersion", usingCurrentVersion)
                .toString();
    }

    public boolean isCanBeImplemented() {
        return canBeImplemented;
    }

    public void setCanBeImplemented(final boolean canBeImplemented) {
        this.canBeImplemented = canBeImplemented;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(final boolean executable) {
        this.executable = executable;
    }

    public boolean isUsingCurrentVersion() {
        return usingCurrentVersion;
    }

    public void setUsingCurrentVersion(final boolean usingCurrentVersion) {
        this.usingCurrentVersion = usingCurrentVersion;
    }
}
