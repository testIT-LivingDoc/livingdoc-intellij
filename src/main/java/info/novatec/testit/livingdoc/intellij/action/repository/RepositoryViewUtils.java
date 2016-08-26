package info.novatec.testit.livingdoc.intellij.action.repository;

import com.intellij.openapi.actionSystem.Presentation;
import info.novatec.testit.livingdoc.intellij.domain.LDNode;
import info.novatec.testit.livingdoc.intellij.domain.LDNodeType;
import info.novatec.testit.livingdoc.intellij.domain.SpecificationNode;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Utility class for repository view tool windows.
 */
public class RepositoryViewUtils {

    private RepositoryViewUtils() {
        // Utility class
    }

    /**
     * Returns the corresponding node icon depending on certain conditions
     *
     * @param specificationNode {@link SpecificationNode}
     * @return {@link Icon}
     */
    public static Icon getNodeIcon(final SpecificationNode specificationNode) {

        Icon icon = Icons.EXECUTABLE;

        if (specificationNode.isExecutable() && specificationNode.isUsingCurrentVersion()) {
            icon = Icons.EXE_WORKING;
        } else if (specificationNode.isExecutable() && specificationNode.canBeImplemented()) {
            icon = Icons.EXE_DIFF;

        } else if (!specificationNode.isExecutable()) {
            icon = Icons.NOT_EXECUTABLE;
        }
        return icon;
    }

    /**
     * Sets the enabled/disabled action property for the executable nodes.
     *
     * @param selectedNodes Selected nodes in the repository view tree.
     * @param presentation  The specific place in the user interface of the action.
     * @see Presentation
     * @see DefaultMutableTreeNode
     */
    public static void setEnabledForExecutableNode(final DefaultMutableTreeNode[] selectedNodes,
                                                   final Presentation presentation) {
        setEnabledForNode(selectedNodes, presentation, true);
    }

    /**
     * Sets the enabled/disabled action property for the specification nodes.
     *
     * @param selectedNodes Selected nodes in the repository view tree.
     * @param presentation  The specific place in the user interface of the action.
     * @see Presentation
     * @see DefaultMutableTreeNode
     */
    public static void setEnabledForSpecificationNode(final DefaultMutableTreeNode[] selectedNodes,
                                                      final Presentation presentation) {
        setEnabledForNode(selectedNodes, presentation, false);
    }

    /**
     * Sets the enabled/disabled action property for the executable nodes that can change their version.
     *
     * @param selectedNodes    Selected nodes in the repository view tree.
     * @param presentation     The specific place in the user interface of the action.
     * @param toCurrentVersion True whether the action changes to current version. False for implemented version.
     */
    public static void setEnabledForNodeVersion(final DefaultMutableTreeNode[] selectedNodes,
                                                final Presentation presentation, final boolean toCurrentVersion) {

        setEnabledForExecutableNode(selectedNodes, presentation);

        boolean enabled = presentation.isEnabled();

        if (enabled) {
            try {
                SpecificationNode specificationNode = (SpecificationNode) selectedNodes[0].getUserObject();
                enabled = specificationNode.canBeImplemented()
                        && (specificationNode.isUsingCurrentVersion() ^ toCurrentVersion);

            } catch (ClassCastException cce) {
                enabled = false;
            }
            presentation.setEnabled(enabled);
        }
    }

    /**
     * Sets the enabled/disabled action property depending on the node type and whether the node is executable.
     *
     * @param selectedNodes       Selected nodes in the repository view tree.
     * @param presentation        {@link Presentation}       The specific place in the user interface of the action.
     * @param executableCondition True for nodes that can be executed. False otherwise.
     * @see LDNodeType
     * @see DefaultMutableTreeNode
     */
    private static void setEnabledForNode(final DefaultMutableTreeNode[] selectedNodes, final Presentation presentation,
                                          final boolean executableCondition) {

        if (ArrayUtils.isEmpty(selectedNodes)) {
            presentation.setEnabled(false);
            return;
        }

        Object userObject = selectedNodes[0].getUserObject();
        boolean enabled = ((LDNode) userObject).getType() == LDNodeType.SPECIFICATION;

        if (enabled && executableCondition) {
            try {
                SpecificationNode specificationNode = (SpecificationNode) userObject;
                enabled = specificationNode.isExecutable();

            } catch (ClassCastException cce) {
                enabled = false;
            }
        }

        presentation.setEnabled(enabled);
    }
}
