package org.livingdoc.intellij.connector.legacy;

import info.novatec.testit.livingdoc.repository.DocumentRepository;
import info.novatec.testit.livingdoc.runner.Main;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.*;
import info.novatec.testit.livingdoc.server.rest.LivingDocRestClient;
import org.jetbrains.annotations.NotNull;
import org.livingdoc.intellij.connector.LivingDocConnector;
import org.livingdoc.intellij.domain.*;
import org.livingdoc.intellij.gui.toolwindows.RepositoryViewUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * TODO document me
 */
public class LegacyLivingDocConnectorImpl implements LivingDocConnector {

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
    public List<String> getAllProjects() throws LivingDocException {

        try {
            Set<Project> projects = livingDocRestClient.getAllProjects(null);
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
    public List<String> getSystemUnderTestsOfProject(@NotNull String projectName) throws LivingDocException {

        try {
            Set<SystemUnderTest> suts = livingDocRestClient.getSystemUnderTestsOfProject(projectName, null);
            List<String> sutNames = new ArrayList<>(suts.size());

            for (SystemUnderTest sut : suts) {
                sutNames.add(sut.getName());
            }
            return sutNames;

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public List<RepositoryNode> getAllRepositoriesForSystemUnderTest(@NotNull final ModuleNode moduleNode) throws LivingDocException {

        try {
            Set<Repository> repositories = livingDocRestClient.getAllRepositoriesForSystemUnderTest(getSystemUnderTest(moduleNode), null);
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
    public void getSpecificationHierarchy(@NotNull final RepositoryNode repositoryNode, @NotNull final ModuleNode moduleNode, @NotNull final DefaultMutableTreeNode parentNode) throws LivingDocException {

        try {
            DocumentNode documentNode = livingDocRestClient.getSpecificationHierarchy(
                    convertToRepository(repositoryNode),
                    getSystemUnderTest(moduleNode), null);

            paintDocumentNode(documentNode.getChildren(), parentNode);

        } catch (LivingDocServerException ldse) {
            throw new LivingDocException(ldse);
        }
    }

    @Override
    public void tagDocumentAsImplemented(@NotNull final SpecificationNode specificationNode) throws Exception {

        RepositoryNode repositoryNode = RepositoryViewUtils.getRepositoryNode(specificationNode);

        DocumentRepository documentRepository = convertToRepository(repositoryNode).asDocumentRepository(
                getClass().getClassLoader(), projectSettings.getUser(), projectSettings.getPassword());

        documentRepository.setDocumentAsImplemented(specificationNode.getNodeName());
    }

    @Override
    public String getSpecificationRemoteUrl(@NotNull final SpecificationNode specificationNode, @NotNull final RepositoryNode repositoryNode) {

        Specification specification = Specification.newInstance(specificationNode.getNodeName());
        specification.setRepository(convertToRepository(repositoryNode));

        return specification.getRepository().getType().resolveName(specification);
    }

    @Override
    public String getLivingDocMainClass() {
        return Main.class.getName();
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

    /**
     * @param childNode  {@link DocumentNode}
     * @param userObject {@link Node}
     * @return {@link SpecificationNode}
     */
    private SpecificationNode convertToSpecificationNode(final DocumentNode childNode, final Node userObject) {

        SpecificationNode specificationNode = new SpecificationNode(childNode.getTitle(), userObject);
        specificationNode.setExecutable(childNode.isExecutable());
        specificationNode.setCanBeImplemented(childNode.isCanBeImplemented() && childNode.isExecutable());
        specificationNode.setIcon(RepositoryViewUtils.getNodeIcon(specificationNode));
        return specificationNode;
    }

    private void sortChildren(DefaultMutableTreeNode node) {

        List<DefaultMutableTreeNode> childrenList = Collections.list(node.children());

        childrenList.sort((o1, o2) ->
                ((Node) o1.getUserObject()).getNodeName().compareToIgnoreCase(((Node) o2.getUserObject()).getNodeName()));

        node.removeAllChildren();
        childrenList.forEach(node::add);
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
