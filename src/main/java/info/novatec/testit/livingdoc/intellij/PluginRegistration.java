package info.novatec.testit.livingdoc.intellij;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import info.novatec.testit.livingdoc.intellij.action.IdentifyProjectAction;
import info.novatec.testit.livingdoc.intellij.action.ServerConfigurationAction;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import org.jetbrains.annotations.NotNull;

/**
 * To register an action on IDEA startup.
 * If you register the PluginRegistration class in the <application-components> section of
 * the plugin.xml file, {@link #initComponent()} method is called on IDEA start-up.
 */
public class PluginRegistration implements ApplicationComponent {

    private static final String IDE_PROJECT_CONTEXTUAL_MENU = "ProjectViewPopupMenuSettingsGroup";
    private static final String IDE_FILE_SETTINGS_MENU = "FileOtherSettingsGroup";


    @Override
    public void initComponent() {

        ActionManager actionManager = ActionManager.getInstance();

        ServerConfigurationAction serverConfigurationAction = new ServerConfigurationAction();
        actionManager.registerAction(PluginProperties.getValue("plugin.action.server.configuration"), serverConfigurationAction);
        DefaultActionGroup fileSettingsMenu = (DefaultActionGroup) actionManager.getAction(IDE_FILE_SETTINGS_MENU);
        fileSettingsMenu.addSeparator();
        fileSettingsMenu.add(serverConfigurationAction);

        IdentifyProjectAction identifyProjectAction = new IdentifyProjectAction();
        actionManager.registerAction(PluginProperties.getValue("plugin.action.identify.project"), identifyProjectAction);
        DefaultActionGroup projectMenu = (DefaultActionGroup) actionManager.getAction(IDE_PROJECT_CONTEXTUAL_MENU);
        projectMenu.addSeparator();
        projectMenu.add(identifyProjectAction);
    }

    @Override
    public void disposeComponent() {
        // Disposes system resources.
    }

    @NotNull
    @Override
    public String getComponentName() {

        return PluginProperties.getValue("plugin.name");
    }
}
