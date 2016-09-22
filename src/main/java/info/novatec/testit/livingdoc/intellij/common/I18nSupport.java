package info.novatec.testit.livingdoc.intellij.common;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Use {@link #getValue(String, Object...)} to get a property defined in {@link #BUNDLE_PATH}
 */
public class I18nSupport {

    private static final String BUNDLE_PATH = "properties.locale";

    @NonNls
    private static final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH);

    private I18nSupport() {
        //Utility class
    }

    public static String getValue
            (@PropertyKey(resourceBundle = BUNDLE_PATH) String key, Object... params) {

        String value = bundle.getString(key);
        if (params.length > 0) {
            return MessageFormat.format(value, params);
        }
        return value;
    }
}
