package info.novatec.testit.livingdoc.intellij.domain;

import info.novatec.testit.livingdoc.intellij.common.Icons;
import info.novatec.testit.livingdoc.intellij.common.NodeType;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Custom tree node for LivingDoc plugin.
 * It's the user object for {@link javax.swing.tree.DefaultMutableTreeNode}
 * Built from {@link info.novatec.testit.livingdoc.server.domain.DocumentNode}
 *
 * @see Node
 */
public class SpecificationNode extends Node {

    private boolean executable;
    private boolean canBeImplemented;
    private boolean usingCurrentVersion;

    public SpecificationNode() {
        super();
    }

    public SpecificationNode(final DocumentNode node, final Node parentNode) {

        super(node.getTitle(), Icons.EXECUTABLE, NodeType.SPECIFICATION, parentNode);

        this.executable = node.isExecutable();
        this.canBeImplemented = node.isCanBeImplemented() && node.isExecutable();
        this.usingCurrentVersion = false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("executable", executable)
                .append("isCanBeImplemented", canBeImplemented)
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
