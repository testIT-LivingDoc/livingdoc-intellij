package info.novatec.testit.livingdoc.intellij.run;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;

/**
 * Creation of run configurations from context
 * TODO review necessary
 *
 * @see RemoteRunConfiguration
 */
public class LivingDocRunConfigurationProducer extends RunConfigurationProducer<RunConfiguration> {

    private static final Logger LOG = Logger.getInstance(LivingDocRunConfigurationProducer.class);


    protected LivingDocRunConfigurationProducer() {
        super(LivingDocConfigurationType.getInstance());
    }

    /**
     * Sets up a configuration based on the specified context.
     *
     * @param configuration a clone of the template run configuration of the specified type
     * @param context       contains the information about a location in the source code.
     * @param sourceElement a reference to the source element for the run configuration (by default contains the element at caret,
     *                      can be updated by the producer to point to a higher-level element in the tree).
     * @return true if the context is applicable to this run configuration producer, false if the context is not applicable and the
     * configuration should be discarded.
     */
    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("LivingDocRunConfigurationProducer.setupConfigurationFromContext() --> " + configuration.getClass().toString());
        }
        return true;
    }

    /**
     * Checks if the specified configuration was created from the specified context.
     *
     * @param configuration a configuration instance.
     * @param context       contains the information about a location in the source code.
     * @return true if this configuration was created from the specified context, false otherwise.
     */
    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        return false;
    }
}
