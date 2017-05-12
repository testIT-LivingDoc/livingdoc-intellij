package org.livingdoc.intellij.connector;

import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.LivingDocVersion;
import org.livingdoc.intellij.connector.legacy.LegacyLivingDocConnectorImpl;
import org.livingdoc.intellij.connector.livingdoc.LivingDocConnectorImpl;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.run.RemoteRunConfiguration;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Collection;

/**
 * Layer for the connection with the LivingDoc core.
 */
public interface LivingDocConnector {

    /**
     * Creates a new LivingDoc connector instance.
     *
     * @param projectSettings {@link ProjectSettings}.
     * @return LivingDocConnector implementation.
     * @see LivingDocVersion
     */
    static LivingDocConnector newInstance(final ProjectSettings projectSettings) {

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

    /**
     * Checks whether the connection can be established.
     *
     * @return true when the connection can be established, false in otherwise.
     * @throws LivingDocException
     */
    boolean testConnection() throws LivingDocException;

    /**
     * Retrieves the list of LivingDoc projects      .
     *
     * @return A {@link Collection} of the {@link String}s with the project name.
     * @throws LivingDocException
     */
    Collection<String> getAllProjects() throws LivingDocException;


    /**
     * Retrieves the system under tests for the project associated.
     *
     * @param projectName The LivingDoc associated project name.
     * @return A {@link Collection} of the {@link String}s with the sut name.
     * @throws LivingDocException
     */
    Collection<String> getSystemUnderTestsForProject(@NotNull final String projectName) throws LivingDocException;

    /**
     * Retrieves the repositories for the project associated with the specified system under test.
     *
     * @param moduleNode {@link ModuleNode} Represents a project module for IntelliJ.
     * @return A {@link Collection} of the {@link RepositoryNode}s.
     * @throws LivingDocException
     */
    Collection<RepositoryNode> getRepositoriesForSystemUnderTest(@NotNull final ModuleNode moduleNode) throws LivingDocException;

    /**
     * Retrieves the whole specification hierarchy for a system under test and builds the specification tree.
     *
     * @param repositoryNode {@link RepositoryNode} Represents a LivingDoc repository.
     * @param moduleNode     {@link ModuleNode} Represents a project module for IntelliJ.
     * @param parentNode     {@link DefaultMutableTreeNode} Represents the root node for the specification tree.
     * @throws LivingDocException
     */
    void buildfSpecificationHierarchy(@NotNull final RepositoryNode repositoryNode, @NotNull final ModuleNode moduleNode, @NotNull final DefaultMutableTreeNode parentNode) throws LivingDocException;

    /**
     * Sets the specification as implemented.
     *
     * @param specificationNode {@link SpecificationNode} The specification.
     */
    void tagDocumentAsImplemented(@NotNull final SpecificationNode specificationNode) throws LivingDocException;

    /**
     * Returns remote URL for the specification.
     *
     * @param specificationNode {@link SpecificationNode} Represents the specification.
     * @param repositoryNode    {@link RepositoryNode} Represents the repository.
     * @return A {@link String} with the specification URL.
     */
    String getSpecificationRemoteUrl(@NotNull final SpecificationNode specificationNode, @NotNull final RepositoryNode repositoryNode);

    /**
     * Returns the main LivingDoc class to execute a specification.
     *
     * @return A {@link String} with the Main class name.
     */
    String getLivingDocMainClass();

    /**
     * Prints the specification on the text-output stream.
     *
     * @param runConfiguration  {@link RemoteRunConfiguration}.
     * @param specificationFile {@link File} Contains the LivingDoc specification.
     */
    void printSpecification(@NotNull final RemoteRunConfiguration runConfiguration, File specificationFile) throws LivingDocException;

    /**
     * Gets the specification execution from the report file.
     *
     * @param runConfiguration {@link RemoteRunConfiguration}.
     * @param reportFile       {@link File} LivingDoc report file.
     * @return The result of the execution in a {@link LivingDocExecution} object.
     * @throws LivingDocException
     */
    LivingDocExecution getSpecificationExecution(RemoteRunConfiguration runConfiguration, File reportFile) throws LivingDocException;
}
