package org.livingdoc.intellij.domain;

import com.intellij.icons.AllIcons;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.livingdoc.intellij.common.NodeType;


public class ModuleNode extends Node {

    private String moduleName;

    public ModuleNode() {
        super();
        moduleName = null;
    }

    public ModuleNode(final String nodeName, final String moduleName) {
        super(nodeName, AllIcons.Nodes.Module, NodeType.MODULE, null);
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("moduleName", moduleName)
                .toString();
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }
}
