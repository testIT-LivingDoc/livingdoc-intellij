package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import info.novatec.testit.livingdoc.intellij.ui.IdentifyProjectUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;

/**
 * Action to open the project configuration for LivingDoc.
 * Accessed from "Project contextual menu" (click on right button on IntelliJ project)
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
            PropertiesComponent properties = PropertiesComponent.getInstance(anActionEvent.getProject());
            properties.setValue(PluginProperties.getValue("livingdoc.project.key"), (String) dialog.getProjectCombo().getSelectedItem());
            properties.setValue(PluginProperties.getValue("livingdoc.system.key"), (String) dialog.getSystemtField().getSelectedItem());
        }
    }
}
