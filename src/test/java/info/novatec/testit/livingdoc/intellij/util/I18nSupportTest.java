package info.novatec.testit.livingdoc.intellij.util;

import org.junit.Assert;
import org.junit.Test;

public class I18nSupportTest {

    @Test
    public void getValue() {
        String result = I18nSupport.getValue("server.configuration.header");
        Assert.assertNotNull(result);
        Assert.assertEquals("LivingDoc", result);
    }
}