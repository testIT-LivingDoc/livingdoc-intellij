package info.novatec.testit.livingdoc.intellij.domain;

import info.novatec.testit.livingdoc.server.domain.Repository;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.swing.*;

/**
 * @see Repository
 */
public class RepositoryNode extends LDNode {

    private static final long serialVersionUID = -5251788072170319409L;

    private Repository repository;
    private boolean local = false;

    public RepositoryNode(final String name, final Icon icon) {
        super(name, icon, LDNodeType.REPOSITORY);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(super.toString())
                .append("repository", repository)
                .append("local", local)
                .toString();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(final boolean local) {
        this.local = local;
    }
}
