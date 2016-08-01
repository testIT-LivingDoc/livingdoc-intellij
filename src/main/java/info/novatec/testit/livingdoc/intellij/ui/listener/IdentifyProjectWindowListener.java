package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import info.novatec.testit.livingdoc.intellij.domain.LDProject;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.ui.IdentifyProjectUI;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Set;

/**
 * When the window is opened, this listener fills the {@link ComboBox}
 * with LivingDoc projects.
 *
 * @see WindowListener
 * @see PluginLivingDocXmlRpcClient
 * TODO Add loading popup
 */
public class IdentifyProjectWindowListener implements WindowListener {

    private static final Logger LOG = Logger.getInstance(IdentifyProjectWindowListener.class);

    private final IdentifyProjectUI identifyProjectUI;
    private final LDProject ldProject;

    public IdentifyProjectWindowListener(IdentifyProjectUI ui) {
        this.identifyProjectUI = ui;
        ldProject = new LDProject(identifyProjectUI.getIdeaProject());
    }

    @Override
    public void windowOpened(WindowEvent e) {
        loadProjects();
        loadCredentials();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    private void loadProjects() {
        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<Project> projects = service.getAllProjects(this.ldProject.getIdentifier());
            for (Project project : projects) {
                identifyProjectUI.getProjectCombo().addItem(project.getName());
            }
            if (StringUtils.isNotBlank(this.ldProject.getLivingDocProject().getName())) {
                identifyProjectUI.getProjectCombo().setSelectedItem(this.ldProject.getLivingDocProject().getName());
            } else {
                identifyProjectUI.getProjectCombo().setSelectedIndex(0);
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(this.ldProject.getIdeaProject(), I18nSupport.getValue("identify.project.error.loading.project.desc"), I18nSupport.getValue("identify.project.error.loading.project"));
            LOG.error(ldse);
        }
    }

    private void loadCredentials() {
        this.identifyProjectUI.getUserTextField().setText(this.ldProject.getUser());
        this.identifyProjectUI.getPassTextField().setText(this.ldProject.getPass());
    }
}
