package info.novatec.testit.livingdoc.intellij.core;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import info.novatec.testit.livingdoc.intellij.gui.settings.ModuleConfigurableImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provider class for module configurable extension defined in <b>plugin.xml</b> with
 * <code>id="LivingDoc.Module.Configurable"</code>.<br>
 * (File > Project Structure > Modules)
 *
 * @see ConfigurableProvider
 * @see ModuleConfigurableImpl
 */
public class ModuleConfigurableProvider extends ConfigurableProvider {

    private final Module module;

    public ModuleConfigurableProvider(@NotNull final Module module) {
        this.module = module;
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        return new ModuleConfigurableImpl(module);
    }
}
