package org.livingdoc.intellij.connector.legacy;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestClient;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.ProjectSettings;

import java.util.Set;

/**
 * TODO document me
 */
public class LegacyLivingDocConnectorImpl implements LivingDocConnector {

    private LivingDocRestClient livingDocRestClient;

    public LegacyLivingDocConnectorImpl(ProjectSettings projectSettings) {
        livingDocRestClient = new LivingDocRestClient(projectSettings.getUrlServer(), projectSettings.getUser(), projectSettings.getPassword());
    }

    @Override
    public boolean testConnection() throws LivingDocServerException {
        return livingDocRestClient.testConnection(null, null);
    }

    @Override
    public Set<Project> getAllProjects() throws LivingDocServerException {
        return livingDocRestClient.getAllProjects(null);
    }

    @Override
    public Set<SystemUnderTest> getSystemUnderTestsOfProject(@NotNull String projectName) throws LivingDocServerException {
        return livingDocRestClient.getSystemUnderTestsOfProject(projectName, null);
    }

    @Override
    public Set<Repository> getAllRepositoriesForSystemUnderTest(SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return livingDocRestClient.getAllRepositoriesForSystemUnderTest(systemUnderTest, null);
    }

    @Override
    public DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return livingDocRestClient.getSpecificationHierarchy(repository, systemUnderTest, null);
    }
}
