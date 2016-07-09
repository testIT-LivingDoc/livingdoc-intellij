package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import info.novatec.testit.livingdoc.intellij.model.LDProject;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
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

    private final ComboBox<String> sutComboBox;
    private final LDProject project;

    public ProjectNameActionListener(final ComboBox<String> systemsComboBox, final Project p) {

        sutComboBox = systemsComboBox;

        project = new LDProject(p);
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {

        sutComboBox.removeAllItems();

        String selectedProject = null;
        if(actionEvent.getSource() instanceof ComboBox) {
            ComboBox<String> projectCombo = (ComboBox<String>)actionEvent.getSource();
            selectedProject = (String)projectCombo.getSelectedItem();
        }

        try {
            RpcClientService service = new PluginLivingDocXmlRpcClient();
            Set<SystemUnderTest> systems = service.getSystemUnderTestsOfProject(selectedProject, project.getIdentifier());
            for(SystemUnderTest system : systems) {
                sutComboBox.addItem(system.getName());
            }
            if(!systems.isEmpty()) {
                sutComboBox.setSelectedItem(project.getSystemUnderTest().getName());
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(ldse.getMessage(), I18nSupport.getValue("identify.project.error.loading.systems"));
            LOG.error(ldse);
        }
    }
}
