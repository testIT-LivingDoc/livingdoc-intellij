package org.livingdoc.intellij.domain;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service implementation for project service extension defined in <b>plugin.xml</b> with
 * <code>id="LivingDoc.Project.Service.Settings"</code>
 * <br/><br/>
 * Passwords are stored in encrypted form using
 * <a href="https://github.com/JetBrains/intellij-community/blob/master/platform/credential-store/readme.md">
 * IntelliJ Platform Credentials Store API</a>
 *
 * @see PersistentStateComponent
 */

@State(name = "LivingDoc")
public final class ProjectSettings implements PersistentStateComponent<ProjectSettings> {

    private static final String LIVINGDOC_SERVICE_NAME = "IntelliJ Platform LivingDoc";

    private String urlServer;
    private String user;
    private boolean isConnected;
    private LivingDocVersion livingDocVersion = LivingDocVersion.LEGACY; //FIXME

    @NotNull
    public static ProjectSettings getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, ProjectSettings.class);
    }

    @Nullable
    @Override
    public ProjectSettings getState() {
        return this;
    }

    @Override
    public void loadState(final ProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getUrlServer() {
        return urlServer;
    }

    public void setUrlServer(final String urlServer) {
        this.urlServer = urlServer;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(final boolean connected) {
        isConnected = connected;
    }

    public String getPassword() {

        if (StringUtils.isBlank(user)) {
            return null;
        }

        CredentialAttributes credentialAttributes = new CredentialAttributes(LIVINGDOC_SERVICE_NAME);

        PasswordSafe passwordSafe = PasswordSafe.getInstance();
        Credentials credentials = passwordSafe.get(credentialAttributes);

        return credentials != null ? credentials.getPasswordAsString() : "";
    }

    public void setPassword(final String password) {

        if (StringUtils.isBlank(user)) {
            return;
        }

        CredentialAttributes credentialAttributes = new CredentialAttributes(LIVINGDOC_SERVICE_NAME);
        Credentials credentials = new Credentials(user, password);

        PasswordSafe passwordSafe = PasswordSafe.getInstance();
        passwordSafe.set(credentialAttributes, credentials);

    }

    public LivingDocVersion getLivingDocVersion() {
        return livingDocVersion;
    }

    public void setLivingDocVersion(LivingDocVersion livingDocVersion) {
        this.livingDocVersion = livingDocVersion;
    }
}
