package info.novatec.testit.livingdoc.intellij.domain;

import javax.swing.*;

/**
 *
 */
public class RootNode extends LDNode {

    private static final long serialVersionUID = 6318342321404200357L;

    public RootNode(String name, Icon icon) {
        super(name, icon, LDNodeType.ROOT);
    }
}
