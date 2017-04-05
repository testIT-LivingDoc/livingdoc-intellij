package org.livingdoc.intellij.connector.livingdoc;

import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.LivingDocException;
import org.livingdoc.intellij.domain.ModuleNode;
import org.livingdoc.intellij.domain.RepositoryNode;
import org.livingdoc.intellij.domain.SpecificationNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

/**
 * TODO document me for LivingDoc2
 */
public class LivingDocConnectorImpl implements LivingDocConnector {

    @Override
    public boolean testConnection() throws LivingDocException {
        return false;
    }

    @Override
    public List<String> getAllProjects() throws LivingDocException {
        return Collections.emptyList();
    }

    @Override
    public List<String> getSystemUnderTestsOfProject(@NotNull final String projectName) throws LivingDocException {
        return Collections.emptyList();
    }

    @Override
    public List<RepositoryNode> getAllRepositoriesForSystemUnderTest(@NotNull final ModuleNode moduleNode) throws LivingDocException {
        return Collections.emptyList();
    }

    @Override
    public void getSpecificationHierarchy(@NotNull RepositoryNode repositoryNode, @NotNull final ModuleNode moduleNode, @NotNull final DefaultMutableTreeNode parentNode) throws LivingDocException {
        // not implemented yet
    }

    @Override
    public void tagDocumentAsImplemented(@NotNull SpecificationNode specificationNode) {
        // not implemented yet
    }

    @Override
    public String getSpecificationRemoteUrl(@NotNull SpecificationNode specificationNode, @NotNull RepositoryNode repositoryNode) {
        return null;
    }

    @Override
    public String getLivingDocMainClass() {
        return null;
    }
}
