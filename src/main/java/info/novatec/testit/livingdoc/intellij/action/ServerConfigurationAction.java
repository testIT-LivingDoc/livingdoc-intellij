package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import info.novatec.testit.livingdoc.intellij.ui.ServerConfigurationUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.Icons;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;

/**
 * Action to open the configuration to connect with LivingDoc Server:<br>
 *     - The base URL of Confluence<br>
 *     - The XML-RPC handler<br>
 * It is mandatory press OK button to save the server configuration.<br>
 * Accessed from File > Other Settings > Livingdoc
 * TODO Better to locate in File > Settings > Tools
 * @see ServerConfigurationUI
 */
public class ServerConfigurationAction extends AnAction {

    public ServerConfigurationAction() {
        super(I18nSupport.getValue("server.configuration.menu.title"), I18nSupport.getValue("server.configuration.menu.tooltip"), Icons.LIVINGDOC);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        ServerConfigurationUI dialog = new ServerConfigurationUI(anActionEvent.getProject());
        dialog.show();

        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            PropertiesComponent properties = PropertiesComponent.getInstance();
            properties.setValue(ServerPropertiesManager.URL, dialog.getUrlTextField().getText());
            properties.setValue(ServerPropertiesManager.HANDLER, dialog.getHandlerTextField().getText());
        }
    }
}
