package info.novatec.testit.livingdoc.intellij.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class I18nSupportTest {

    @Test
    public void getValue() throws Exception {
        String value = I18nSupport.getValue("server.configuration.header");
        Assert.assertNotNull(value);
        Assert.assertEquals("LivingDoc",value);
    }

}