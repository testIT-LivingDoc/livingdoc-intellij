package info.novatec.testit.livingdoc.intellij.action.repository;

import info.novatec.testit.livingdoc.intellij.domain.Node;
import info.novatec.testit.livingdoc.intellij.domain.RepositoryNode;

public class RepositoryViewUtils {

    /**
     * Recursive method to find the node's repository through node's parent.
     *
     * @param node {@link Node} Livingdoc specification node
     * @return @{@link RepositoryNode}
     */
    public static RepositoryNode getRepositoryNode(final Node node) {
        if (node.getParent() instanceof RepositoryNode) {
            return (RepositoryNode) node.getParent();
        } else {
            return getRepositoryNode((Node) node.getParent());
        }
    }
}
