package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.ui.listener.IdentifyProjectWindowListener;
import info.novatec.testit.livingdoc.intellij.ui.listener.ProjectNameActionListener;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.UIUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * User Interface for IntelliJ IDEA project configuration.
 * The dialog's layout is a {@link GridBagLayout}.
 *
 * @see DialogWrapper
 */
public class IdentifyProjectUI extends DialogWrapper {

    private final Project ideaProject;
    private JPanel jPanel;
    private ComboBox<String> projectCombo;
    private ComboBox<String> systemCombo;
    private JBTextField userTextField;
    private JBPasswordField passTextField;

    public IdentifyProjectUI(Project p) {
        super(p);
        ideaProject = p;

        setTitle(I18nSupport.getValue("identify.project.header"));

        addComponents();
        addListeners();

        init();
    }

    public Project getIdeaProject() {
        return ideaProject;
    }

    public JBTextField getUserTextField() {
        return userTextField;
    }

    public JPasswordField getPassTextField() {
        return passTextField;
    }

    public ComboBox<String> getProjectCombo() {
        return projectCombo;
    }

    public ComboBox getSystemField() {
        return systemCombo;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return jPanel;
    }

    private void addListeners() {
        getWindow().addWindowListener(new IdentifyProjectWindowListener(this));
        projectCombo.addActionListener(new ProjectNameActionListener(this.systemCombo, this.ideaProject));
    }

    private void addComponents() {

        jPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        JBLabel titleLabel = new JBLabel(I18nSupport.getValue("identify.project.title"));
        titleLabel.setForeground(new JBColor(JBColor.BLACK, JBColor.ORANGE));
        titleLabel.setFont(new Font(null, Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(titleLabel, constraints);
        UIUtils.insertSpace(jPanel, 1, 2);

        JBLabel infoLabel = new JBLabel(I18nSupport.getValue("identify.project.info.1"));
        constraints.gridy = 2;
        jPanel.add(infoLabel, constraints);

        infoLabel = new JBLabel(I18nSupport.getValue("identify.project.info.2"));
        constraints.gridy = 3;
        jPanel.add(infoLabel, constraints);
        UIUtils.insertSpace(jPanel, 4, 2);

        JBLabel projectLabel = new JBLabel(I18nSupport.getValue("identify.project.field.project.label"));
        projectLabel.setLabelFor(this.projectCombo);
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        jPanel.add(projectLabel, constraints);

        projectCombo = new ComboBox<>();
        projectCombo.setMinimumAndPreferredWidth(200);
        constraints.gridx = 1;
        jPanel.add(projectCombo, constraints);

        JBLabel systemLabel = new JBLabel(I18nSupport.getValue("identify.project.field.system.label") + " ");
        systemLabel.setLabelFor(this.systemCombo);
        constraints.gridx = 0;
        constraints.gridy = 6;
        jPanel.add(systemLabel, constraints);

        systemCombo = new ComboBox<>();
        systemCombo.setMinimumAndPreferredWidth(200);
        constraints.gridx = 1;
        jPanel.add(systemCombo, constraints);
        UIUtils.insertSpace(jPanel, 7, 2);

        JBLabel userLabel = new JBLabel(I18nSupport.getValue("identify.project.field.user.label") + " ");
        userLabel.setLabelFor(userTextField);
        constraints.gridx = 0;
        constraints.gridy = 8;
        jPanel.add(userLabel, constraints);

        userTextField = new JBTextField();
        userTextField.setColumns(14);
        constraints.gridx = 1;
        jPanel.add(userTextField, constraints);

        JBLabel passLabel = new JBLabel(I18nSupport.getValue("identify.project.field.pass.label") + " ");
        passLabel.setLabelFor(passTextField);
        constraints.gridx = 0;
        constraints.gridy = 9;
        jPanel.add(passLabel, constraints);

        passTextField = new JBPasswordField();
        passTextField.setColumns(20);
        constraints.gridx = 1;
        jPanel.add(passTextField, constraints);
        UIUtils.insertSpace(jPanel, 10, 2);
    }
}
