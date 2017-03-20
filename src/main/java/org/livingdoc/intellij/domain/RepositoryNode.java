package org.livingdoc.intellij.domain;

import com.intellij.icons.AllIcons;
import info.novatec.testit.livingdoc.server.domain.Repository;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.NodeType;

/**
 * @see Repository
 */
public class RepositoryNode extends Node {

    private Repository repository;

    public RepositoryNode() {
        super();
    }

    public RepositoryNode(final String name, final Node moduleNode) {
        super(name, AllIcons.Nodes.PpLibFolder, NodeType.REPOSITORY, moduleNode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("repository", repository)
                .toString();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(final Repository repository) {
        this.repository = repository;
    }
}
