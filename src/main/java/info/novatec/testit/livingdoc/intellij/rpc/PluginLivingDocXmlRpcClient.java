package info.novatec.testit.livingdoc.intellij.rpc;

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

    private static final String IDENTIFIER = "WORKSPACE";

    public PluginLivingDocXmlRpcClient(@NotNull final com.intellij.openapi.project.Project project) {

        super(new ServerPropertiesManagerImpl(project));
    }

    public Set<Project> getAllProjects() throws LivingDocServerException {
        return super.getAllProjects(IDENTIFIER);
    }

    public Set<SystemUnderTest> getSystemUnderTestsOfProject(@NotNull final String projectName) throws LivingDocServerException {
        return super.getSystemUnderTestsOfProject(projectName, IDENTIFIER);
    }

    public Set<Repository> getAllRepositoriesForSystemUnderTest(final SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return super.getAllRepositoriesForSystemUnderTest(systemUnderTest, IDENTIFIER);
    }

    public DocumentNode getSpecificationHierarchy(final Repository repository, final SystemUnderTest systemUnderTest) throws LivingDocServerException {
        return super.getSpecificationHierarchy(repository, systemUnderTest, IDENTIFIER);
    }
}
