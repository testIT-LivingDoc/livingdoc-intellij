package info.novatec.testit.livingdoc.intellij.util;

import info.novatec.testit.livingdoc.intellij.common.I18nSupport;
import org.junit.Assert;
import org.junit.Test;

public class I18nSupportTest {

    @Test
    public void getValue() {
        String result = I18nSupport.getValue("module.settings.tab.title");
        Assert.assertNotNull(result);
        Assert.assertEquals("LivingDoc", result);
    }
}