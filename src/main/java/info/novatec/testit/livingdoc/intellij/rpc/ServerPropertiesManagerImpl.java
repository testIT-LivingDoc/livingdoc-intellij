package info.novatec.testit.livingdoc.intellij.rpc;

import com.intellij.ide.util.PropertiesComponent;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;

/**
 * {@link PropertiesComponent} is used for plugin properties persistence.
 * @see ServerPropertiesManager
 */
public class ServerPropertiesManagerImpl implements ServerPropertiesManager {

    private final PropertiesComponent properties;

    public ServerPropertiesManagerImpl() {
        properties = PropertiesComponent.getInstance();
    }

    @Override
    public String getProperty(final String key, final String identifier) {
        return properties.getValue(key);
    }

    @Override
    public void setProperty(final String key, final String value, final String identifier) {
        // properties are read-only
    }
}
