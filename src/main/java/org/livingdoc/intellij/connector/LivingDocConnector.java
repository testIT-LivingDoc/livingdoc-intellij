package org.livingdoc.intellij.connector;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.legacy.LegacyLivingDocConnectorImpl;
import org.livingdoc.intellij.connector.livingdoc.LivingDocConnectorImpl;
import org.livingdoc.intellij.domain.ProjectSettings;

import java.util.Set;

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

    boolean testConnection() throws LivingDocServerException;

    Set<Project> getAllProjects() throws LivingDocServerException;

    Set<SystemUnderTest> getSystemUnderTestsOfProject(@NotNull String projectName) throws LivingDocServerException;

    Set<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest systemUnderTest) throws LivingDocServerException;

    DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest) throws LivingDocServerException;
}
