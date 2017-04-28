package org.livingdoc.intellij.connector.livingdoc;

import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.run.RemoteRunConfiguration;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * {@link LivingDocConnector} implementation for the new LivingDoc version.
 */
public class LivingDocConnectorImpl implements LivingDocConnector {

    @Override
    public boolean testConnection() throws LivingDocException {
        return false;
    }

    @Override
    public Collection<String> getAllProjects() throws LivingDocException {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getSystemUnderTestsForProject(@NotNull final String projectName) throws LivingDocException {
        return Collections.emptyList();
    }

    @Override
    public Collection<RepositoryNode> getRepositoriesForSystemUnderTest(@NotNull final ModuleNode moduleNode) throws LivingDocException {
        return Collections.emptyList();
    }

    @Override
    public void buildfSpecificationHierarchy(@NotNull RepositoryNode repositoryNode, @NotNull final ModuleNode moduleNode, @NotNull final DefaultMutableTreeNode parentNode) throws LivingDocException {
        // not implemented yet
    }

    @Override
    public void tagDocumentAsImplemented(@NotNull final SpecificationNode specificationNode) {
        // not implemented yet
    }

    @Override
    public String getSpecificationRemoteUrl(@NotNull final SpecificationNode specificationNode, @NotNull final RepositoryNode repositoryNode) {
        return null;
    }

    @Override
    public String getLivingDocMainClass() {
        return null;
    }

    @Override
    public void printSpecification(@NotNull final RemoteRunConfiguration runConfiguration, @NotNull final File specificationFile) {
        // not implemented yet
    }

    @Override
    public LivingDocExecution getSpecificationExecution(@NotNull final RemoteRunConfiguration runConfiguration, @NotNull final File reportFile) {
        return null;
    }
}
