package info.novatec.testit.livingdoc.intellij.core;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTypeLivingDocTest {

    @Test
    public void getInstance() {
        ConfigurationTypeLivingDoc configurationTypeLivingDoc = new ConfigurationTypeLivingDoc();
        Assert.assertNotNull(configurationTypeLivingDoc);
    }

}