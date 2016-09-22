package info.novatec.testit.livingdoc.intellij;

import com.intellij.openapi.components.ApplicationComponent;
import info.novatec.testit.livingdoc.intellij.common.PluginProperties;
import org.jetbrains.annotations.NotNull;

/**
 * To register an action on IDEA startup.
 * If you register the PluginRegistration class in the <application-components> section of
 * the plugin.xml file, {@link #initComponent()} method is called on IDEA start-up.
 */
public class PluginRegistration implements ApplicationComponent {

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
        // Disposes system resources.
    }

    @NotNull
    @Override
    public String getComponentName() {

        return PluginProperties.getValue("plugin.id");
    }
}
