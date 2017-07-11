package org.livingdoc.intellij.connector.legacy;

import com.intellij.openapi.diagnostic.Logger;
import info.novatec.testit.livingdoc.document.Document;
import info.novatec.testit.livingdoc.report.XmlReport;
import info.novatec.testit.livingdoc.repository.DocumentRepository;
import info.novatec.testit.livingdoc.runner.Main;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.*;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestClient;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;
import org.livingdoc.intellij.run.RemoteRunConfiguration;
import org.xml.sax.SAXException;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link LivingDocConnector} implementation for the legacy LivingDoc version.
 */
public class LegacyLivingDocConnectorImpl implements LivingDocConnector {

    private static final Logger LOG = Logger.getInstance(LegacyLivingDocConnectorImpl.class);

    private final ProjectSettings projectSettings;
    private final LivingDocRestClient livingDocRestClient;


    public LegacyLivingDocConnectorImpl(ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
        livingDocRestClient = new LivingDocRestClient(projectSettings.getUrlServer(), projectSettings.getUser(), projectSettings.getPassword());
    }

    @Override
    public boolean testConnection() throws LivingDocException {

        try {
            return livingDocRestClient.testConnection(null, null);

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public Collection<String> getAllProjects() throws LivingDocException {

        try {
            Collection<Project> projects = livingDocRestClient.getAllProjects(null);
            List<String> projectNames = new ArrayList<>(projects.size());

            for (Project project : projects) {
                projectNames.add(project.getName());
            }
            return projectNames;

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public Collection<String> getSystemUnderTestsForProject(@NotNull final String projectName) throws LivingDocException {

        try {
            Collection<SystemUnderTest> systemUnderTests = livingDocRestClient.getSystemUnderTestsOfProject(projectName, null);
            List<String> sutNames = new ArrayList<>(systemUnderTests.size());

            for (SystemUnderTest sut : systemUnderTests) {
                sutNames.add(sut.getName());
            }
            return sutNames;

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public Collection<RepositoryNode> getRepositoriesForSystemUnderTest(@NotNull final ModuleNode moduleNode) throws LivingDocException {

        SystemUnderTest systemUnderTest = getSystemUnderTest(moduleNode);

        try {
            Collection<Repository> repositories = livingDocRestClient.getAllRepositoriesForSystemUnderTest(systemUnderTest, null);
            List<RepositoryNode> repositoryNodes = new ArrayList<>(repositories.size());

            for (Repository repository : repositories) {
                repositoryNodes.add(convertToRepositoryNode(repository));
            }
            return repositoryNodes;

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public void buildfSpecificationHierarchy(@NotNull final RepositoryNode repositoryNode, @NotNull final ModuleNode moduleNode, @NotNull final DefaultMutableTreeNode parentNode) throws LivingDocException {

        Repository repository = convertToRepository(repositoryNode);
        SystemUnderTest systemUnderTest = getSystemUnderTest(moduleNode);

        try {
            DocumentNode documentNode = livingDocRestClient.getSpecificationHierarchy(repository, systemUnderTest, null);

            paintDocumentNode(documentNode.getChildren(), parentNode);

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public void tagDocumentAsImplemented(@NotNull final SpecificationNode specificationNode) throws LivingDocException {

        RepositoryNode repositoryNode = RepositoryViewUtils.getRepositoryNode(specificationNode);
        Repository repository = convertToRepository(repositoryNode);

        DocumentRepository documentRepository = repository.asDocumentRepository(
                getClass().getClassLoader(), projectSettings.getUser(), projectSettings.getPassword());

        try {
            documentRepository.setDocumentAsImplemented(specificationNode.getNodeName());

        } catch (Exception e) {
            throw new LivingDocException(e);
        }
    }

    @Override
    public String getSpecificationRemoteUrl(@NotNull final SpecificationNode specificationNode, @NotNull final RepositoryNode repositoryNode) {

        Specification specification = Specification.newInstance(specificationNode.getNodeName());
        specification.setRepository(convertToRepository(repositoryNode));

        Repository repository = convertToRepository(repositoryNode);
        return repository.getType().resolveName(specification);
    }

    @Override
    public String getLivingDocMainClass() {
        return Main.class.getName();
    }

    @Override
    public void printSpecification(@NotNull final RemoteRunConfiguration runConfiguration, @NotNull final File specificationFile) throws LivingDocException {

        ClassLoader classLoader = getClass().getClassLoader();

        Repository repository = convertToRepository(runConfiguration);

        DocumentRepository documentRepository = repository.asDocumentRepository(
                classLoader, projectSettings.getUser(), projectSettings.getPassword());

        String location = runConfiguration.getSpecificationName() + (runConfiguration.isCurrentVersion() ? "?implemented=false" : "");

        try (PrintWriter printWriter = new PrintWriter(specificationFile, String.valueOf(StandardCharsets.UTF_8))) {

            Document document = documentRepository.loadDocument(location);

            if (document != null) {
                document.print(printWriter);
            } else {
                LOG.error(I18nSupport.getValue("run.execution.error.document.null"));
            }
        } catch (Exception e) {
            throw new LivingDocException(e);
        }
    }

    @Override
    public LivingDocExecution getSpecificationExecution(@NotNull final RemoteRunConfiguration runConfiguration, @NotNull final File reportFile) throws LivingDocException {

        XmlReport xmlReport;
        try {
            xmlReport = XmlReport.parse(reportFile);

        } catch (SAXException | IOException e) {
            throw new LivingDocException(e);
        }

        Specification specification = Specification.newInstance(runConfiguration.getSpecificationName());
        Repository repository = convertToRepository(runConfiguration);
        specification.setRepository(repository);

        Execution execution = Execution.newInstance(specification, null, xmlReport);
        execution.setResults(xmlReport.getResults(0));  // TODO review index 0 for getResults()

        specification.addExecution(execution);

        return convertToLivingDocExecution(execution);
    }

    /**
     * This recursive method adds a node into the repository tree.<br>
     * Only the executable nodes or nodes with children will be painted.
     *
     * @param children   {@link java.util.List}
     * @param parentNode {@link DefaultMutableTreeNode} Parent node of children nodes indicated in the first parameter.
     * @see DocumentNode
     */
    private void paintDocumentNode(List<DocumentNode> children, @NotNull final DefaultMutableTreeNode parentNode) {

        children.stream().filter(child -> child.isExecutable() || (!child.isExecutable() && child.hasChildren())).forEach(child -> {

            SpecificationNode ldNode = convertToSpecificationNode(child, (Node) parentNode.getUserObject());
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(ldNode);
            parentNode.add(childNode);

            if (child.hasChildren()) {
                paintDocumentNode(child.getChildren(), childNode);
            }
        });
        sortChildren(parentNode);
    }

    private void sortChildren(@NotNull final DefaultMutableTreeNode node) {

        List<DefaultMutableTreeNode> childrenList = Collections.list(node.children());

        childrenList.sort((o1, o2) ->
                ((Node) o1.getUserObject()).getNodeName().compareToIgnoreCase(((Node) o2.getUserObject()).getNodeName()));

        node.removeAllChildren();
        childrenList.forEach(node::add);
    }

    private LivingDocExecution convertToLivingDocExecution(Execution execution) {
        LivingDocExecution livingDocExecution = new LivingDocExecution();
        livingDocExecution.setExecutionErrorId(execution.getExecutionErrorId());
        livingDocExecution.setResults(execution.getResults());
        livingDocExecution.setErrors(execution.getErrors());
        livingDocExecution.setFailures(execution.getFailures());
        livingDocExecution.setSuccess(execution.getSuccess());
        livingDocExecution.setIgnored(execution.getIgnored());
        livingDocExecution.setHasException(execution.hasException());
        livingDocExecution.setHasFailed(execution.hasFailed());
        return livingDocExecution;
    }

    private SpecificationNode convertToSpecificationNode(final DocumentNode childNode, final Node userObject) {

        SpecificationNode specificationNode = new SpecificationNode(childNode.getTitle(), userObject);
        specificationNode.setExecutable(childNode.isExecutable());
        specificationNode.setCanBeImplemented(childNode.isCanBeImplemented() && childNode.isExecutable());
        specificationNode.setIcon(RepositoryViewUtils.getNodeIcon(specificationNode));

        if(specificationNode.isCanBeImplemented()) {
            specificationNode.setUsingCurrentVersion(true);
        }
        return specificationNode;
    }


    private Repository convertToRepository(@NotNull final RemoteRunConfiguration runConfiguration) {

        Repository repository = Repository.newInstance(runConfiguration.getRepositoryUID());
        repository.setName(runConfiguration.getRepositoryName());
        repository.setBaseTestUrl(runConfiguration.getRepositoryURL());
        RepositoryType repositoryType = new RepositoryType();
        repositoryType.setClassName(runConfiguration.getRepositoryClass());
        repository.setType(repositoryType);
        return repository;
    }

    private Repository convertToRepository(@NotNull final RepositoryNode repositoryNode) {

        Repository repository = Repository.newInstance(repositoryNode.getUid());
        repository.setName(repositoryNode.getName());
        repository.setBaseTestUrl(repositoryNode.getBaseTestUrl());
        repository.setBaseRepositoryUrl(repositoryNode.getBaseRepositoryUtl());

        RepositoryType repositoryType = RepositoryType.newInstance(repositoryNode.getTypeName());
        repositoryType.setClassName(repositoryNode.getTypeClassName());
        repositoryType.setDocumentUrlFormat(repositoryNode.getTypeDocumentUrlFormat());
        repository.setType(repositoryType);

        repository.setUsername(projectSettings.getUser());
        repository.setPassword(projectSettings.getPassword());

        return repository;
    }

    private RepositoryNode convertToRepositoryNode(@NotNull final Repository repository) {
        RepositoryNode repositoryNode = new RepositoryNode(repository.getProject().getName());
        repositoryNode.setUid(repository.getUid());
        repositoryNode.setName(repository.getName());
        repositoryNode.setBaseTestUrl(repository.getBaseTestUrl());
        repositoryNode.setBaseRepositoryUtl(repository.getBaseRepositoryUrl());

        repositoryNode.setTypeClassName(repository.getType().getClassName());
        repositoryNode.setTypeName(repository.getType().getName());
        repositoryNode.setTypeDocumentUrlFormat(repository.getType().getDocumentUrlFormat());

        return repositoryNode;
    }

    private SystemUnderTest getSystemUnderTest(@NotNull final ModuleNode moduleNode) {
        SystemUnderTest systemUnderTest = SystemUnderTest.newInstance(moduleNode.getSystemUnderTest());
        systemUnderTest.setProject(getProject(moduleNode));
        return systemUnderTest;
    }

    private Project getProject(@NotNull final ModuleNode moduleNode) {
        return Project.newInstance(moduleNode.getProject());
    }
}
