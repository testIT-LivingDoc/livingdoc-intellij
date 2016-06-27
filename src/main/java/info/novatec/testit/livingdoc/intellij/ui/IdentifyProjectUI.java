package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.ui.listener.ProjectNameActionListener;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.UIUtils;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Created by mruiz on 19/06/2016.
 */
public class IdentifyProjectUI extends DialogWrapper {

    private final JPanel jPanel;
    private JBLabel loadLabel;
    private ComboBox projectField;
    private ComboBox systemField;

    public IdentifyProjectUI(Project project) {
        super(project);

        setTitle(I18nSupport.i18n_str("identify.project.header"));

        jPanel = new JPanel(new GridBagLayout());
        addComponents();
        addListeners();

        init();
    }

    // TODO refactoring this method
    public void loadProjects() {
        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            Set<info.novatec.testit.livingdoc.server.domain.Project> projects = service.getAllProjects(PluginLivingDocXmlRpcClient.IDENTIFIER);
            for(info.novatec.testit.livingdoc.server.domain.Project project : projects) {
                projectField.addItem(project.getName());
            }
        } catch (LivingDocServerException ldse) {
            Messages.showMessageDialog(ldse.getMessage(), "Error", Messages.getErrorIcon());

        } finally {
            jPanel.remove(loadLabel);
        }
    }

    private void addListeners() {
        projectField.addActionListener(new ProjectNameActionListener(this.systemField));
    }

    private void addComponents() {
        GridBagConstraints constraints = new GridBagConstraints();

        // Tittle section
        JBLabel titleLabel = new JBLabel(I18nSupport.i18n_str("identify.project.title"));
        // Green for regular theme and white for dark theme
        titleLabel.setForeground(new JBColor(JBColor.BLACK, JBColor.ORANGE));
        titleLabel.setFont(new Font(null, Font.BOLD, 20));
        constraints.gridx = 0; // column
        constraints.gridy = 0; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(titleLabel, constraints);
        UIUtils.insertSpace(jPanel, 1, 2);

        // Info header section
        constraints.gridx = 0; // column
        constraints.gridy = 2; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("identify.project.info.1")), constraints);

        constraints.gridx = 0; // column
        constraints.gridy = 3; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("identify.project.info.2")), constraints);
        UIUtils.insertSpace(jPanel, 4, 2);

        // Project Name
        constraints.gridx = 0; // column
        constraints.gridy = 5; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("identify.project.field.project.label")), constraints);

        projectField = new ComboBox();
        projectField.setMinimumAndPreferredWidth(200);
        constraints.gridx = 1; // column
        constraints.gridy = 5; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(projectField, constraints);

        // System under test
        constraints.gridx = 0; // column
        constraints.gridy = 6; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("identify.project.field.system.label")+ " "), constraints);

        systemField = new ComboBox();
        systemField.setMinimumAndPreferredWidth(200);
        constraints.gridx = 1; // column
        constraints.gridy = 6; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(systemField, constraints);
        UIUtils.insertSpace(jPanel, 7, 2);

        // Repositories
        JBTable repositoriesTable = new JBTable();

        constraints.gridx = 1; // column
        constraints.gridy = 8; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(repositoriesTable, constraints);
        UIUtils.insertSpace(jPanel, 9, 2);

        loadLabel = new JBLabel(I18nSupport.i18n_str("identify.project.load.info")+ " ");
        constraints.gridx = 0; // column
        constraints.gridy = 10; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(loadLabel, constraints);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return jPanel;
    }
}
