package info.novatec.testit.livingdoc.intellij.domain;

import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.domain.Repository;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @see Repository
 */
public class RepositoryNode extends LDNode {

    private static final long serialVersionUID = -5251788072170319409L;

    private Repository repository;

    public RepositoryNode(final String name) {
        super(name, Icons.REPOSITORY, LDNodeType.REPOSITORY);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(super.toString())
                .append("repository", repository)
                .toString();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
