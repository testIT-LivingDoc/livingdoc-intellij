package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.impl.ToolWindowImpl;
import info.novatec.testit.livingdoc.intellij.ui.RepositoryViewUI;
import info.novatec.testit.livingdoc.intellij.util.PluginIcons;

/**
 * Created by mruiz on 18/06/2016.
 */
public class RepositoryViewAction extends AnAction {

    public RepositoryViewAction() {

        // TODO Extract string to locale file
        super("xRepository _View", "xTODO", PluginIcons.LIVINGDOC_OPTION);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        RepositoryViewUI repositoryViewUI = new RepositoryViewUI();
    }
}
