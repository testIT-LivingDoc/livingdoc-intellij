package org.livingdoc.intellij.connector;

import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.legacy.LegacyLivingDocConnectorImpl;
import org.livingdoc.intellij.connector.livingdoc.LivingDocConnectorImpl;
import org.livingdoc.intellij.domain.*;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * TODO document me
 */
public interface LivingDocConnector {

    static LivingDocConnector newInstance(ProjectSettings projectSettings) {

        LivingDocConnector connector;

        switch (projectSettings.getLivingDocVersion()) {
            case LEGACY:
                connector = new LegacyLivingDocConnectorImpl(projectSettings);
                break;
            case LIVINGDOC2:
                connector = new LivingDocConnectorImpl();
                break;
            default:
                connector = null;
        }
        return connector;
    }

    boolean testConnection() throws LivingDocException;

    List<String> getAllProjects() throws LivingDocException;

    List<String> getSystemUnderTestsOfProject(@NotNull final String projectName) throws LivingDocException;

    List<RepositoryNode> getAllRepositoriesForSystemUnderTest(@NotNull final ModuleNode moduleNode) throws LivingDocException;

    void getSpecificationHierarchy(@NotNull final RepositoryNode repositoryNode, @NotNull final ModuleNode moduleNode, @NotNull final DefaultMutableTreeNode parentNode) throws LivingDocException;

    void tagDocumentAsImplemented(@NotNull final SpecificationNode specificationNode) throws Exception;

    String getSpecificationRemoteUrl(@NotNull final SpecificationNode specificationNode, @NotNull final RepositoryNode repositoryNode);

    String getLivingDocMainClass();
}
