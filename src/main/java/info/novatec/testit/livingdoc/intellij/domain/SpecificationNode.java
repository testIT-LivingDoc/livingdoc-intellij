package info.novatec.testit.livingdoc.intellij.domain;

import info.novatec.testit.livingdoc.intellij.common.Icons;
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

    private final boolean executable;
    private final boolean canBeImplemented;
    private boolean usingCurrentVersion;

    public SpecificationNode(DocumentNode node, Node parentNode) {

        super(node.getTitle(), Icons.EXECUTABLE, NodeType.SPECIFICATION, parentNode);

        this.executable = node.isExecutable();
        this.canBeImplemented = node.canBeImplemented() && node.isExecutable();
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

    public boolean canBeImplemented() {
        return canBeImplemented;
    }

    public boolean isExecutable() {
        return executable;
    }

    public boolean isUsingCurrentVersion() {
        return usingCurrentVersion;
    }

    public void setUsingCurrentVersion(final boolean usingCurrentVersion) {
        this.usingCurrentVersion = usingCurrentVersion;
    }
}
