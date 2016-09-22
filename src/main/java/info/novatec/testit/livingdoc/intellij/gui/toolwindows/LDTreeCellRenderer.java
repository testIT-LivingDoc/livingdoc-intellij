package info.novatec.testit.livingdoc.intellij.gui.toolwindows;

import info.novatec.testit.livingdoc.intellij.domain.Node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Custom tree cell renderer for {@link ToolWindowPanel}.
 * It uses the {@link Node} properties to render.
 *
 * @see DefaultTreeCellRenderer
 */
public class LDTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 8133945553515022452L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        Object object = ((DefaultMutableTreeNode) value).getUserObject();

        if (object instanceof Node) {

            Node node = (Node) object;

            setText(node.getName());
            setIcon(node.getIcon());
        }
        return component;
    }
}
