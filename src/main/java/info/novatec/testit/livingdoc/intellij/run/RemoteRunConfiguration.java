package info.novatec.testit.livingdoc.intellij.run;

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
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.domain.Repository;
import info.novatec.testit.livingdoc.server.domain.RepositoryType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A named run configuration which can be executed. <br>
 * The {@link #getConfigurationEditor()} method returns the settings editor component (user interface) for the run
 * configuration settings (Displayed in multiple tabs if there's more than one):
 * <ul>
 * <li>{@link LivingDocSettingsEditor}</li>
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

    private String user;
    private String pass;

    private TestStatusLine statusLine;


    RemoteRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, final String name) {
        super(name, project, factory);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {

        SettingsEditorGroup<RemoteRunConfiguration> settingsEditorGroup = new SettingsEditorGroup<>();
        settingsEditorGroup.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"), new LivingDocSettingsEditor(getProject()));
        settingsEditorGroup.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());
        return settingsEditorGroup;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {

        if (StringUtils.isBlank(repositoryUID)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.main.field.repositoryuid.error"));
        }
        if (StringUtils.isBlank(repositoryURL)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.main.field.repositoryurl.error"));
        }
        if (StringUtils.isBlank(specificationName)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.main.field.specificationName.error"));
        }
        if (StringUtils.isBlank(repositoryClass)) {
            throw new RuntimeConfigurationException(I18nSupport.getValue("run.configuration.main.field.repositoryclass.error"));
        }

        super.checkConfiguration();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new LivingDocRunProfileState(environment);
    }

    public Repository getRepository() {
        Repository repository = Repository.newInstance(repositoryUID);
        repository.setName(repositoryName);
        repository.setBaseTestUrl(repositoryURL);
        RepositoryType repositoryType = new RepositoryType();
        repositoryType.setClassName(repositoryClass);
        repository.setType(repositoryType);
        return repository;
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

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(final String pass) {
        this.pass = pass;
    }

    public boolean isCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(final boolean currentVersion) {
        this.currentVersion = currentVersion;
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
}
