package info.novatec.testit.livingdoc.intellij.model;

import com.intellij.ide.util.PropertiesComponent;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * LivingDoc project common properties.
 * {@link PropertiesComponent} is used to get these properties of IntelliJ persistence.
 * You can use {@link #saveProperties(String, String, String, String)} to save the project properties with
 * {@link PropertiesComponent}
 * @see PluginProperties
 */
public class LDProject implements Serializable{

    private static final long serialVersionUID = -980439077995379150L;

    private static final String SYSTEM_KEY = "livingdoc.system.key";
    private static final String PROJECT_KEY = "livingdoc.project.key";
    private static final String IDENTIFIER_KEY = "livingdoc.identifier";
    private static final String USERNAME_KEY = "livingdoc.username.key";
    private static final String PASSWORD_KEY = "livingdoc.password.key";

    private boolean configuredProject = true;
    private String identifier;
    private String user;
    private String pass;
    private Project livingDocProject;
    private SystemUnderTest systemUnderTest;
    private final com.intellij.openapi.project.Project ideaProject;
    private PropertiesComponent properties;

    public LDProject(com.intellij.openapi.project.Project project) {
        this.ideaProject = project;

        load();
    }

    public void saveProperties(final String projectName, final String systemName, final String userName,
                               final String password) {

        properties.setValue(PluginProperties.getValue(PROJECT_KEY), projectName);
        properties.setValue(PluginProperties.getValue(SYSTEM_KEY), systemName);
        properties.setValue(PluginProperties.getValue(USERNAME_KEY), userName);
        properties.setValue(PluginProperties.getValue(PASSWORD_KEY), password);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("configuredProject", configuredProject)
                .append("identifier", identifier)
                .append("user", user)
                .append("pass", pass)
                .append("livingDocProject", livingDocProject)
                .append("systemUnderTest", systemUnderTest)
                .append("ideaProject", ideaProject)
                .append("properties", properties)
                .toString();
    }

    public SystemUnderTest getSystemUnderTest() {
        return this.systemUnderTest;
    }

    public String getIdentifier() {
        return identifier;
    }

    public com.intellij.openapi.project.Project getIdeaProject() {
        return ideaProject;
    }

    public Project getLivingDocProject() {
        return livingDocProject;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public boolean isConfiguredProject() {
        return configuredProject;
    }

    private void load() {
        identifier = PluginProperties.getValue(IDENTIFIER_KEY);

        properties = PropertiesComponent.getInstance(this.ideaProject);

        String systemName = properties.getValue(PluginProperties.getValue(SYSTEM_KEY));
        systemUnderTest = SystemUnderTest.newInstance(systemName);

        String projectName = properties.getValue(PluginProperties.getValue(PROJECT_KEY));
        livingDocProject = Project.newInstance(projectName);

        systemUnderTest.setProject(livingDocProject);

        user = properties.getValue(PluginProperties.getValue(USERNAME_KEY));
        pass = properties.getValue(PluginProperties.getValue(PASSWORD_KEY));

        configuredProject = StringUtils.isNotBlank(systemName);
    }
}
