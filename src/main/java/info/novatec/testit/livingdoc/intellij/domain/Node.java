package info.novatec.testit.livingdoc.intellij.domain;

import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Custom tree node for LivingDoc plugin.
 * It's the user object for {@link javax.swing.tree.DefaultMutableTreeNode}
 * Built from {@link info.novatec.testit.livingdoc.server.domain.DocumentNode}
 *
 * @see LDNode
 */
public class Node extends LDNode {

    private static final long serialVersionUID = -8016091757391516275L;

    private LDNode parent;
    private boolean executable;
    private boolean canBeImplemented;
    private boolean usingCurrentVersion;

    public Node(DocumentNode node, LDNode parentNode) {

        super(node.getTitle(), Icons.EXECUTABLE, LDNodeType.SPECIFICATION);

        this.parent = parentNode;
        this.executable = node.isExecutable();
        this.canBeImplemented = node.canBeImplemented();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(super.toString())
                .append("parent", parent)
                .append("executable", executable)
                .append("canBeImplemented", canBeImplemented)
                .append("usingCurrentVersion", usingCurrentVersion)
                .toString();
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

    public LDNode getParent() {
        return parent;
    }

    public void setParent(LDNode parent) {
        this.parent = parent;
    }

    public boolean isUsingCurrentVersion() {
        return usingCurrentVersion;
    }

    public void setUsingCurrentVersion(final boolean usingCurrentVersion) {
        this.usingCurrentVersion = usingCurrentVersion;
    }
}
