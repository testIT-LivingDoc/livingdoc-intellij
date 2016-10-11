package info.novatec.testit.livingdoc.intellij.core;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.mock.MockProject;
import com.intellij.mock.MockProjectEx;
import com.intellij.openapi.extensions.Extensions;
import info.novatec.testit.livingdoc.intellij.common.MockDisposable;
import org.junit.Assert;
import org.junit.Test;

public class RemoteConfigurationFactoryTest {


    @Test
    public void createTemplateConfiguration() {

        Extensions.registerAreaClass("IDEA_PROJECT", null);

        MockDisposable mockDisposable = new MockDisposable();
        MockProject project = new MockProjectEx(mockDisposable);

        ConfigurationTypeLivingDoc configurationTypeLivingDoc = new ConfigurationTypeLivingDoc();
        RemoteConfigurationFactory remoteConfigurationFactory = new RemoteConfigurationFactory(configurationTypeLivingDoc);
        RunConfiguration runConfiguration = remoteConfigurationFactory
                .createTemplateConfiguration(project);

        Assert.assertNotNull(runConfiguration);
    }
}