package info.novatec.testit.livingdoc.intellij.gui.toolwindows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.Presentation;
import info.novatec.testit.livingdoc.intellij.common.Icons;
import info.novatec.testit.livingdoc.intellij.domain.*;
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

    public static ModuleNode getModuleNode(final Node node) {

        if (node.getParent().getType() == NodeType.MODULE) {
            return (ModuleNode) node.getParent();
        } else {
            return getModuleNode(node.getParent());
        }
    }

    public static Node getErrorNode(final String descError) {
        return new Node(descError, Icons.ERROR, NodeType.ERROR, null);
    }

    /**
     * Recursive method to find the specificationNode's repository through specificationNode's parent.
     *
     * @param specificationNode {@link SpecificationNode} Livingdoc specification specificationNode
     * @return @{@link RepositoryNode}
     */
    public static RepositoryNode getRepositoryNode(final SpecificationNode specificationNode) {
        if (specificationNode.getParent() instanceof RepositoryNode) {
            return (RepositoryNode) specificationNode.getParent();
        } else {
            return getRepositoryNode((SpecificationNode) specificationNode.getParent());
        }
    }

    /**
     * Returns the corresponding node icon depending on the result of the execution
     *
     * @param hasError          True if the execution fails. False otherwise.
     * @param specificationNode {@link SpecificationNode}
     * @return {@link Icon}
     */
    public static Icon getResultIcon(final boolean hasError, final SpecificationNode specificationNode) {

        Icon icon = hasError ? Icons.ERROR : Icons.SUCCESS;

        if (specificationNode.isUsingCurrentVersion()) {
            icon = hasError ? Icons.ERROR_WORKING : Icons.SUCCESS_WORKING;
        } else if (specificationNode.canBeImplemented()) {
            icon = hasError ? Icons.ERROR_DIFF : Icons.SUCCESS_DIFF;
        }
        return icon;
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
            icon = AllIcons.Nodes.Folder;
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

            Object userObject = selectedNodes[0].getUserObject();
            if (userObject instanceof SpecificationNode) {
                SpecificationNode specificationNode = (SpecificationNode) userObject;
                enabled = specificationNode.canBeImplemented()
                        && (specificationNode.isUsingCurrentVersion() ^ toCurrentVersion);
            } else {
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
     * @see NodeType
     * @see DefaultMutableTreeNode
     */
    private static void setEnabledForNode(final DefaultMutableTreeNode[] selectedNodes, final Presentation presentation,
                                          final boolean executableCondition) {

        if (ArrayUtils.isEmpty(selectedNodes)) {
            presentation.setEnabled(false);
            return;
        }

        Object userObject = selectedNodes[0].getUserObject();
        boolean enabled = ((Node) userObject).getType() == NodeType.SPECIFICATION;

        if (enabled && executableCondition) {

            if (userObject instanceof SpecificationNode) {

                SpecificationNode specificationNode = (SpecificationNode) userObject;
                enabled = specificationNode.isExecutable();

            } else {
                enabled = false;
            }
        }
        presentation.setEnabled(enabled);
    }
}
