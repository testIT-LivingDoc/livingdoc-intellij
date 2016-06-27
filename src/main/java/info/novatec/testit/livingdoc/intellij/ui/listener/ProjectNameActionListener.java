package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * Created by mruiz on 19/06/2016.
 */
public class ProjectNameActionListener implements ActionListener {

    private ComboBox systemsCombo;

    public ProjectNameActionListener(ComboBox systems) {
        systemsCombo = systems;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param actionEvent
     */
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        RpcClientService service = new PluginLivingDocXmlRpcClient();
        ComboBox projectCombo = (ComboBox)actionEvent.getSource();
        String selectedProject = (String)projectCombo.getSelectedItem();

        try {
            Set<SystemUnderTest> systems = service.getSystemUnderTestsOfProject(selectedProject, PluginLivingDocXmlRpcClient.IDENTIFIER);
            for(SystemUnderTest system : systems) {
                systemsCombo.addItem(system.getName());
            }
        } catch (LivingDocServerException ldse) {
            // TODO Extract string to locale file
            Messages.showMessageDialog(ldse.getMessage(), "Error", Messages.getErrorIcon());
        }
    }
}
