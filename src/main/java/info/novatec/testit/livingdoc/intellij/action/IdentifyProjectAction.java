package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import info.novatec.testit.livingdoc.intellij.domain.LDProject;
import info.novatec.testit.livingdoc.intellij.ui.IdentifyProjectUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;

/**
 * Action to open the project configuration for LivingDoc.
 * Accessed from "Project contextual menu" (click on right button on IntelliJ project)
 *
 * @see IdentifyProjectUI
 */
public class IdentifyProjectAction extends AnAction {

    public IdentifyProjectAction() {
        super(I18nSupport.getValue("identify.project.menu.title"), I18nSupport.getValue("identify.project.menu.tooltip"), Icons.LIVINGDOC);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        IdentifyProjectUI dialog = new IdentifyProjectUI(anActionEvent.getProject());
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            LDProject project = new LDProject(anActionEvent.getProject());
            project.saveProperties(
                    (String) dialog.getProjectCombo().getSelectedItem(),
                    (String) dialog.getSystemField().getSelectedItem(),
                    dialog.getUserTextField().getText(),
                    String.valueOf(dialog.getPassTextField().getPassword()));
        }
    }
}
