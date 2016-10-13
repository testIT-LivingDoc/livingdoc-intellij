package info.novatec.testit.livingdoc.intellij.rpc;

import info.novatec.testit.livingdoc.intellij.common.PluginProperties;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.DocumentNode;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.LivingDocXmlRpcClient;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Extends to use a {@link info.novatec.testit.livingdoc.server.ServerPropertiesManager} implementation
 *
 * @see LivingDocXmlRpcClient
 * @see ServerPropertiesManagerImpl
 */
public class PluginLivingDocXmlRpcClient extends LivingDocXmlRpcClient {

    private final String identifier;
    private final String handler;


    public PluginLivingDocXmlRpcClient(@NotNull final com.intellij.openapi.project.Project project) {

        super(new ServerPropertiesManagerImpl(project));

        this.handler = PluginProperties.getValue("livingdoc.handler.default");
        this.identifier = PluginProperties.getValue("livingdoc.identifier.default");
    }

    public boolean testConnection(final String hostName) throws LivingDocServerException {
        return super.testConnection(hostName, handler);
    }

    public Set<Project> getAllProjects() throws LivingDocServerException {
        return super.getAllProjects(identifier);
    }

    public Set<SystemUnderTest> getSystemUnderTestsOfProject(@NotNull final String projectName) throws LivingDocServerException {
        return super.getSystemUnderTestsOfProject(projectName, identifier);
    }

    public Set<Repository> getAllRepositoriesForSystemUnderTest(final SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return super.getAllRepositoriesForSystemUnderTest(systemUnderTest, identifier);
    }

    public DocumentNode getSpecificationHierarchy(final Repository repository, final SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return super.getSpecificationHierarchy(repository, systemUnderTest, identifier);
    }
}
