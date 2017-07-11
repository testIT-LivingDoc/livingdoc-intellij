package org.livingdoc.intellij.common;

import org.junit.Assert;
import org.junit.Test;

public class I18nSupportTest {

    @Test
    public void getValue() {

        String result = I18nSupport.getValue("module.settings.tab.title");

        Assert.assertNotNull(result);
        Assert.assertEquals("LivingDoc", result);
    }

    @Test
    public void getValueWithParams() {

        String result1 = I18nSupport.getValue("global.settings.button.test", "par1", "par2");

        Assert.assertNotNull(result1);
        Assert.assertEquals("Test Connection", result1);
    }
}