package org.livingdoc.intellij.run;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.ExecutionBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.ui.TestStatusLine;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.livingdoc.intellij.common.I18nSupport;
import org.livingdoc.intellij.domain.ExecutionCounter;
import org.livingdoc.intellij.domain.SpecificationNode;
import org.livingdoc.intellij.gui.runconfiguration.RunConfigurationEditor;

/**
 * A named run configuration which can be executed. <br>
 * The {@link #getConfigurationEditor()} method returns the settings editor component (user interface) for the run
 * configuration settings (Displayed in multiple tabs if there's more than one):
 * <ul>
 * <li>{@link RunConfigurationEditor}</li>
 * <li>{@link LogConfigurationPanel}</li>
 * </ul>
 *
 * @see ApplicationConfiguration
 * @see RunConfiguration
 */
public class RemoteRunConfiguration extends ApplicationConfiguration {

    private String repositoryUID;
    private String repositoryURL;
    private String repositoryClass;
    private String repositoryName;
    private String specificationName;

    private boolean currentVersion;

    private TestStatusLine statusLine;
    private ExecutionCounter executionCounter;
    private SpecificationNode selectedNode;


    public RemoteRunConfiguration(final Project project, final ConfigurationFactory factory, final String name) {
        super(name, project, factory);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {

        SettingsEditorGroup<RemoteRunConfiguration> settingsEditorGroup = new SettingsEditorGroup<>();
        settingsEditorGroup.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new RunConfigurationEditor(getProject()));
        settingsEditorGroup.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());
        return settingsEditorGroup;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

        if (StringUtils.isBlank(repositoryUID)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.error.repository.uid"));
        }
        if (StringUtils.isBlank(repositoryURL)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.error.repository.url"));
        }
        if (StringUtils.isBlank(specificationName)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.error.specification"));
        }
        if (StringUtils.isBlank(repositoryClass)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.error.repository.class"));
        }

        super.checkConfiguration();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new RunProfileStateLivingDoc(environment);
    }

    public String getRepositoryUID() {
        return repositoryUID;
    }

    public void setRepositoryUID(final String repositoryUID) {
        this.repositoryUID = repositoryUID;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

    public void setRepositoryURL(final String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }

    public String getSpecificationName() {
        return specificationName;
    }

    public void setSpecificationName(final String specificationName) {
        this.specificationName = specificationName;
    }

    public String getRepositoryClass() {
        return repositoryClass;
    }

    public void setRepositoryClass(final String repositoryClass) {
        this.repositoryClass = repositoryClass;
    }

    public boolean isCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(final boolean currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public void setRepositoryName(final String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public TestStatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(TestStatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public SpecificationNode getSelectedNode() {
        return this.selectedNode;
    }

    public void setSelectedNode(final SpecificationNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public ExecutionCounter getExecutionCounter() {
        return this.executionCounter;
    }

    public void setExecutionCounter(ExecutionCounter executionCounter) {
        this.executionCounter = executionCounter;
    }
}
