package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import info.novatec.testit.livingdoc.intellij.ui.ServerConfigurationUI;
import info.novatec.testit.livingdoc.intellij.util.PluginIcons;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;

/**
 * Created by mruiz on 18/06/2016.
 */
public class ServerConfigurationAction extends AnAction {

    public ServerConfigurationAction() {

        // TODO Extract string to locale file
        super("_LivingDoc", "", PluginIcons.LIVINGDOC_OPTION);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        ServerConfigurationUI dialog = new ServerConfigurationUI(anActionEvent.getProject());
        dialog.show();

        if (dialog.getExitCode() == 0) {
            // if the user click on 'OK' button, save properties.
            PropertiesComponent properties = PropertiesComponent.getInstance();
            properties.setValue(ServerPropertiesManager.URL, dialog.getUrlTextField().getText());
            properties.setValue(ServerPropertiesManager.HANDLER, dialog.getHandlerTextField().getText());
        }
    }
}
