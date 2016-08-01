package info.novatec.testit.livingdoc.intellij.util;

import org.junit.Assert;
import org.junit.Test;

public class I18nSupportTest {

    @Test
    public void getValue() {
        String value = I18nSupport.getValue("server.configuration.header");
        Assert.assertNotNull(value);
        Assert.assertEquals("LivingDoc", value);
    }
}