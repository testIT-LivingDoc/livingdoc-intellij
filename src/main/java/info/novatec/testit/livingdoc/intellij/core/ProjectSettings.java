package info.novatec.testit.livingdoc.intellij.core;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Service implementation for project service extension defined in <b>plugin.xml</b> with
 * <code>id="LivingDoc.Project.Service.Settings"</code>
 *
 * @see PersistentStateComponent
 */

@State(name = "LivingDoc")
public final class ProjectSettings implements PersistentStateComponent<ProjectSettings> {

    private String urlServer;
    private String handler;

    @NotNull
    public static ProjectSettings getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, ProjectSettings.class);
    }

    @Nullable
    @Override
    public ProjectSettings getState() {
        return null;
    }

    @Override
    public void loadState(ProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getUrlServer() {
        return urlServer;
    }

    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
