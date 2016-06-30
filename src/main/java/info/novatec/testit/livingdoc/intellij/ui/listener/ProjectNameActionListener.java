package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Listener for selected item event on "Project Name" field
 * @see ActionListener
 */
public class ProjectNameActionListener implements ActionListener {

    private static final Logger LOG = Logger.getInstance(ProjectNameActionListener.class);

    private final ComboBox<String> systemsCombo;
    private final Project project;
    private final String identifier;
    private final String systemsKey;

    public ProjectNameActionListener(final ComboBox<String> systems, final Project p) {

        systemsCombo = systems;
        project = p;
        identifier = PluginProperties.getValue("livingdoc.identifier");
        systemsKey = PluginProperties.getValue("livingdoc.system.key");
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {

        systemsCombo.removeAllItems();

        RpcClientService service = new PluginLivingDocXmlRpcClient();
        ComboBox<String> projectCombo = (ComboBox<String>)actionEvent.getSource();
        String selectedProject = (String)projectCombo.getSelectedItem();

        try {
            Set<SystemUnderTest> systems = service.getSystemUnderTestsOfProject(selectedProject, identifier);
            for(SystemUnderTest system : systems) {
                systemsCombo.addItem(system.getName());
            }
            if(!systems.isEmpty()) {
                PropertiesComponent properties = PropertiesComponent.getInstance(project);
                systemsCombo.setSelectedItem(properties.getValue(systemsKey));
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(ldse.getMessage(), I18nSupport.getValue("identify.project.error.loading.systems"));
            LOG.error(ldse);
        }
    }
}
