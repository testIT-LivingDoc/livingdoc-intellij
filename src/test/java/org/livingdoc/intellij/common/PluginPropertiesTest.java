package org.livingdoc.intellij.common;

import org.junit.Assert;
import org.junit.Test;

public class PluginPropertiesTest {

    @Test
    public void getValue() {

        String propertyValue = PluginProperties.getValue("livingdoc.dir.project");

        Assert.assertNotNull(propertyValue);
        Assert.assertEquals("LivingDoc", propertyValue);
    }
}