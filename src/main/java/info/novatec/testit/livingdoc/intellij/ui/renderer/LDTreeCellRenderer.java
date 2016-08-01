package info.novatec.testit.livingdoc.intellij.ui.renderer;

import info.novatec.testit.livingdoc.intellij.domain.LDNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Custom tree cell renderer for {@link info.novatec.testit.livingdoc.intellij.ui.RepositoryViewUI}.
 * It uses the {@link LDNode} properties to render.
 *
 * @see DefaultTreeCellRenderer
 */
public class LDTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 8133945553515022452L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        Object object = ((DefaultMutableTreeNode) value).getUserObject();
        if (object instanceof LDNode) {
            setText(((LDNode) object).getName());
            setIcon(((LDNode) object).getIcon());
        }
        return component;
    }
}
