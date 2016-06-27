package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import info.novatec.testit.livingdoc.intellij.ui.IdentifyProjectUI;
import info.novatec.testit.livingdoc.intellij.util.PluginIcons;

/**
 * Created by mruiz on 19/06/2016.
 */
public class IdentifyProjectAction extends AnAction {

    public IdentifyProjectAction() {

        // TODO Extract string to locale file
        super("_LivingDoc", "", PluginIcons.LIVINGDOC_OPTION);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        IdentifyProjectUI dialog = new IdentifyProjectUI(anActionEvent.getProject());
        dialog.show();

        dialog.loadProjects();
    }
}
