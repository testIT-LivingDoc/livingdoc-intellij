package info.novatec.testit.livingdoc.intellij.core;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.mock.MockProject;
import com.intellij.mock.MockProjectEx;
import com.intellij.openapi.extensions.Extensions;
import org.junit.Assert;
import org.junit.Test;
import unit.util.MockDisposable;

public class RemoteConfigurationFactoryTest {


    @Test
    public void createTemplateConfiguration() {
        Extensions.registerAreaClass("IDEA_PROJECT", null);
        MockDisposable mockDisposable = new MockDisposable();
        MockProject project = new MockProjectEx(new MockDisposable());

        ConfigurationTypeLivingDoc configurationTypeLivingDoc = new ConfigurationTypeLivingDoc();
        RemoteConfigurationFactory remoteConfigurationFactory = new RemoteConfigurationFactory(configurationTypeLivingDoc);
        RunConfiguration runConfiguration = remoteConfigurationFactory
                .createTemplateConfiguration(project);

        Assert.assertNotNull(runConfiguration);
    }

}