package info.novatec.testit.livingdoc.intellij.domain;

import com.intellij.icons.AllIcons;
import info.novatec.testit.livingdoc.intellij.common.NodeType;
import org.apache.commons.lang3.builder.ToStringBuilder;


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
