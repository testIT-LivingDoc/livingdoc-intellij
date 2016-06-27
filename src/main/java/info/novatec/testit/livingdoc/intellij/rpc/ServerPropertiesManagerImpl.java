package info.novatec.testit.livingdoc.intellij.rpc;

import com.intellij.ide.util.PropertiesComponent;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;

/**
 * Created by mruiz on 18/06/2016.
 */
public class ServerPropertiesManagerImpl implements ServerPropertiesManager {

    final private PropertiesComponent properties;


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
