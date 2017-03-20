package org.livingdoc.intellij.rest;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestClient;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.domain.ProjectSettings;

import java.util.Set;

/***
 * @see LivingDocRestClient
 */
public class PluginLivingDocRestClient extends LivingDocRestClient {


    public PluginLivingDocRestClient(@NotNull final ProjectSettings projectSettings) {

        super(projectSettings.getUrlServer(), projectSettings.getUser(), projectSettings.getPassword());
    }

    public boolean testConnection() throws LivingDocServerException {
        return super.testConnection(null, null);
    }

    public Set<Project> getAllProjects() throws LivingDocServerException {
        return super.getAllProjects(null);
    }

    public Set<SystemUnderTest> getSystemUnderTestsOfProject(@NotNull final String projectName) throws LivingDocServerException {
        return super.getSystemUnderTestsOfProject(projectName, null);
    }

    public Set<Repository> getAllRepositoriesForSystemUnderTest(final SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return super.getAllRepositoriesForSystemUnderTest(systemUnderTest, null);
    }

    public DocumentNode getSpecificationHierarchy(final Repository repository, final SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return super.getSpecificationHierarchy(repository, systemUnderTest, null);
    }
}
