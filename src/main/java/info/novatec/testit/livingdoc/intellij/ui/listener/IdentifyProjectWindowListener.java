package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

/**
 * When the window is opened, this listener fills the {@link ComboBox}
 * with LivingDoc projects.
 * @see WindowListener
 * @see PluginLivingDocXmlRpcClient
 * TODO Add loading popup
 */
public class IdentifyProjectWindowListener implements WindowListener {

    private static final Logger LOG = Logger.getInstance(IdentifyProjectWindowListener.class);

    private final ComboBox<String> projectCombo;
    private final com.intellij.openapi.project.Project project;
    private final String identifier;
    private final String projectKey;

    public IdentifyProjectWindowListener(ComboBox<String> combo, com.intellij.openapi.project.Project p) {
        this.projectCombo = combo;
        this.project = p;
        this.identifier = PluginProperties.getValue("livingdoc.identifier");
        this.projectKey = PluginProperties.getValue("livingdoc.project.key");
    }

    @Override
    public void windowOpened(WindowEvent e) {
        loadProjects();
    }

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}

    private void loadProjects() {
        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<Project> projects = service.getAllProjects(this.identifier);
            for (Project project : projects) {
                projectCombo.addItem(project.getName());
            }
            if (!projects.isEmpty()) {
                PropertiesComponent properties = PropertiesComponent.getInstance(this.project);
                projectCombo.setSelectedItem(properties.getValue(this.projectKey));
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(this.project,ldse.getMessage(), I18nSupport.getValue("identify.project.error.loading.project"));
            LOG.error(ldse);
        }
    }
}
