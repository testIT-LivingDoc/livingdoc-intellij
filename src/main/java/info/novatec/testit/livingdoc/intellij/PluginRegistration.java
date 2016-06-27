package info.novatec.testit.livingdoc.intellij;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import info.novatec.testit.livingdoc.intellij.action.IdentifyProjectAction;
import info.novatec.testit.livingdoc.intellij.action.RepositoryViewAction;
import info.novatec.testit.livingdoc.intellij.action.ServerConfigurationAction;
import info.novatec.testit.livingdoc.intellij.util.PluginActions;
import org.jetbrains.annotations.NotNull;

/**
 * To register an action on IDEA startup.
 * (Register this class in the <application-components> section of the plugin.xml file.)
 * Created by mruiz on 18/06/2016.
 *
 * TODO Review plugin.xml file, pending vendor email and some description
 */
public class PluginRegistration implements ApplicationComponent {

    // If you register the PluginRegistration class in the <application-components> section of
    // the plugin.xml file, this method is called on IDEA start-up.
    @Override
    public void initComponent() {

        ActionManager actionManager = ActionManager.getInstance();

        // Actions
        RepositoryViewAction repositoryViewAction = new RepositoryViewAction();
        ServerConfigurationAction serverConfigurationAction = new ServerConfigurationAction();
        IdentifyProjectAction identifyProjectAction = new IdentifyProjectAction();

        // Register Actions
        actionManager.registerAction(PluginActions.LIVINGDOC_REPOSITORY_VIEW, repositoryViewAction);
        actionManager.registerAction(PluginActions.LIVINGDOC_SERVER_CONFIGURATION, serverConfigurationAction);
        actionManager.registerAction(PluginActions.LIVINGDOC_IDENTIFY_PROJECT, identifyProjectAction);

        DefaultActionGroup livingDocMainMenu = (DefaultActionGroup) actionManager.getAction(PluginActions.LIVINGDOC_MAIN_MENU);
        livingDocMainMenu.add(repositoryViewAction);

        // File > Other Settings > LivingDoc
        DefaultActionGroup fileSettingsMenu = (DefaultActionGroup) actionManager.getAction(PluginActions.IDE_FILE_SETTINGS_MENU);
        fileSettingsMenu.addSeparator();
        fileSettingsMenu.add(serverConfigurationAction);

        // click on right button on project
        DefaultActionGroup projectMenu = (DefaultActionGroup) actionManager.getAction(PluginActions.IDE_PROJECT_MENU);
        projectMenu.addSeparator();
        projectMenu.add(identifyProjectAction);
    }

    // Disposes system resources.
    @Override
    public void disposeComponent() {

    }

    // Returns the component name (any unique string value).
    @NotNull
    @Override
    public String getComponentName() {
        return PluginActions.PLUGIN_NAME;
    }
}
