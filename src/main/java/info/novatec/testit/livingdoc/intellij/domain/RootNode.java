package info.novatec.testit.livingdoc.intellij.domain;

import com.intellij.icons.AllIcons;

/**
 * Root node for repository view tree. <br>
 * Usually this node will be the java project.
 * @see LDNode
 */
public class RootNode extends LDNode {

    private static final long serialVersionUID = 6318342321404200357L;

    public RootNode(final String name) {
        super(name, AllIcons.Nodes.Project, LDNodeType.ROOT);
    }
}
