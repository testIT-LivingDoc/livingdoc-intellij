package info.novatec.testit.livingdoc.intellij.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import info.novatec.testit.livingdoc.intellij.common.Icons;
import info.novatec.testit.livingdoc.intellij.gui.toolwindows.ToolWindowPanel;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class for tool window configured in plugin.xml with id="Repository View".
 *
 * @see ToolWindowFactory
 */
public class ToolWindowFactoryImpl implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        final ToolWindowPanel toolWindowPanel = new ToolWindowPanel(project);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowPanel, project.getName(), false);
        content.setIcon(Icons.LIVINGDOC);
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
        content.setCloseable(true);
        toolWindow.getContentManager().addContent(content);
    }
}
