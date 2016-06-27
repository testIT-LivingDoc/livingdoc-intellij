package info.novatec.testit.livingdoc.intellij.util;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created by mruiz on 19/06/2016.
 */
public class I18nSupport {

    @NonNls
    private static final ResourceBundle bundle = ResourceBundle.getBundle("resources.locale");

    public static String i18n_str
            (@PropertyKey(resourceBundle = "resources.locale") String key, Object... params) {

        String value = bundle.getString(key);
        if (params.length > 0) {
            return MessageFormat.format(value, params);
        }
        return value;
    }
}
