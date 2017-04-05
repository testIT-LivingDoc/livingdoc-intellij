package org.livingdoc.intellij.domain;

import com.intellij.icons.AllIcons;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.NodeType;


public class RepositoryNode extends Node {

    private String uid;
    private String baseTestUrl;
    private String name;

    private String typeClassName;
    private String typeName;
    private String typeDocumentUrlFormat;
    private String baseRepositoryUtl;


    public RepositoryNode(final String nodeName) {
        super(nodeName, AllIcons.Nodes.PpLibFolder, NodeType.REPOSITORY);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("uid", uid)
                .append("baseTestUrl", baseTestUrl)
                .append("name", name)
                .append("typeClassName", typeClassName)
                .append("typeName", typeName)
                .append("typeDocumentUrlFormat", typeDocumentUrlFormat)
                .append("baseRepositoryUtl", baseRepositoryUtl)
                .toString();
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getBaseTestUrl() {
        return baseTestUrl;
    }

    public void setBaseTestUrl(final String baseTestUrl) {
        this.baseTestUrl = baseTestUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeClassName() {
        return typeClassName;
    }

    public void setTypeClassName(final String typeClassName) {
        this.typeClassName = typeClassName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDocumentUrlFormat() {
        return typeDocumentUrlFormat;
    }

    public void setTypeDocumentUrlFormat(String typeDocumentUrlFormat) {
        this.typeDocumentUrlFormat = typeDocumentUrlFormat;
    }

    public String getBaseRepositoryUtl() {
        return baseRepositoryUtl;
    }

    public void setBaseRepositoryUtl(String baseRepositoryUtl) {
        this.baseRepositoryUtl = baseRepositoryUtl;
    }
}
