package org.livingdoc.intellij.connector.livingdoc;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.LivingDocConnector;

import java.util.Set;

/**
 * TODO document me
 */
public class LivingDocConnectorImpl implements LivingDocConnector {

    @Override
    public boolean testConnection() throws LivingDocServerException {
        // TODO for LivingDoc 2
        return false;
    }

    @Override
    public Set<Project> getAllProjects() throws LivingDocServerException {
        // TODO for LivingDoc 2
        return null;
    }

    @Override
    public Set<SystemUnderTest> getSystemUnderTestsOfProject(@NotNull String projectName) throws LivingDocServerException {
        // TODO for LivingDoc 2
        return null;
    }

    @Override
    public Set<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest systemUnderTest) throws LivingDocServerException {
        // TODO for LivingDoc 2
        return null;
    }

    @Override
    public DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest) throws LivingDocServerException {
        // TODO for LivingDoc 2
        return null;
    }
}
