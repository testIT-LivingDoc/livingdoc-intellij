package info.novatec.testit.livingdoc.intellij.common;

import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Use {@link #getValue(String)} to get a property defined in {@link #PROPERTIES_PATH}
 */
public class PluginProperties {

    private static final Logger LOG = Logger.getInstance(PluginProperties.class);

    private static final String PROPERTIES_PATH = "properties/config.properties";
    private static Properties properties;

    public static String getValue(final String key) {
        if (properties == null) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    private static void loadProperties() {
        properties = new Properties();
        try {
            InputStream inStream = PluginProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_PATH);
            properties.load(inStream);

        } catch (IOException ioe) {
            LOG.error(ioe);
        }
    }
}
