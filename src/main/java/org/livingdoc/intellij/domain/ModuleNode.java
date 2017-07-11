package org.livingdoc.intellij.domain;

import com.intellij.icons.AllIcons;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.NodeType;


public class ModuleNode extends Node {

    private String moduleName;
    private String systemUnderTest;
    private String project;

    public ModuleNode() {
        // just for unit testing;
    }

    public ModuleNode(final String nodeName, final String moduleName) {
        super(nodeName, AllIcons.Nodes.Module, NodeType.MODULE, null);
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("moduleName", moduleName)
                .append("systemUnderTest", systemUnderTest)
                .append("project", project)
                .toString();
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

    public String getSystemUnderTest() {
        return systemUnderTest;
    }

    public void setSystemUnderTest(final String systemUnderTest) {
        this.systemUnderTest = systemUnderTest;
    }

    public String getProject() {
        return this.project;
    }

    public void setProject(final String project) {
        this.project = project;
    }
}
