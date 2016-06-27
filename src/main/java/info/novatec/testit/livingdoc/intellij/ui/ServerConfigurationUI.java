package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.ui.listener.TestConnectionActionListener;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.UIUtils;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mruiz on 18/06/2016.
 */
public class ServerConfigurationUI extends DialogWrapper {

    private final JPanel jPanel;
    private String url;
    private String handler;
    private JBTextField urlTextField;
    private JBTextField handlerTextField;
    private JButton testButton;

    public ServerConfigurationUI(Project project) {
        super(project);

        this.preferenceInitializer();

        setTitle(I18nSupport.i18n_str("server.configuration.header"));

        jPanel = new JPanel(new GridBagLayout());
        addComponents();
        addListeners();

        init();
    }

    private void addListeners() {
        testButton.addActionListener(new TestConnectionActionListener(urlTextField.getText(), handlerTextField.getText()));
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return jPanel;
    }

    private void addComponents() {

        GridBagConstraints constraints = new GridBagConstraints();

        // Tittle section
        JBLabel titleLabel = new JBLabel(I18nSupport.i18n_str("server.configuration.title"));
        // Green for regular theme and white for dark theme
        titleLabel.setForeground(new JBColor(JBColor.BLACK, JBColor.ORANGE));
        titleLabel.setFont(new Font(null, Font.BOLD, 20));
        constraints.gridx = 0; // column
        constraints.gridy = 0; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(titleLabel, constraints);
        UIUtils.insertSpace(jPanel, 1, 2);

        // Info header section
        constraints.gridx = 0; // column
        constraints.gridy = 2; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("server.configuration.info")), constraints);

        constraints.gridx = 0; // column
        constraints.gridy = 3; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("server.configuration.info.1")), constraints);

        constraints.gridx = 0; // column
        constraints.gridy = 4; // file
        constraints.gridwidth = 2; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("server.configuration.info.2")), constraints);
        UIUtils.insertSpace(jPanel, 5, 2);

        // Context Path
        constraints.gridx = 0; // column
        constraints.gridy = 6; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("server.configuration.field.url.label")), constraints);

        urlTextField = new JBTextField();
        urlTextField.setName(ServerPropertiesManager.URL);
        urlTextField.setText(this.url);
        urlTextField.setColumns(20);
        constraints.gridx = 1; // column
        constraints.gridy = 6; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(urlTextField, constraints);

        // Xml RPC Handler
        constraints.gridx = 0; // column
        constraints.gridy = 7; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(new JBLabel(I18nSupport.i18n_str("server.configuration.field.handler.label")), constraints);

        handlerTextField = new JBTextField();
        handlerTextField.setName(ServerPropertiesManager.HANDLER);
        handlerTextField.setText(this.handler);
        handlerTextField.setColumns(20);
        constraints.gridx = 1; // column
        constraints.gridy = 7; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(handlerTextField, constraints);
        UIUtils.insertSpace(jPanel, 8, 2);

        // Test Connection
        testButton = new JButton();
        testButton.setText(I18nSupport.i18n_str("server.configuration.button.test.label"));
        constraints.gridx = 1; // column
        constraints.gridy = 9; // file
        constraints.gridwidth = 1; // columns
        constraints.gridheight = 1; // rows
        constraints.anchor = GridBagConstraints.EAST;
        jPanel.add(testButton, constraints);
        UIUtils.insertSpace(jPanel, 10, 2);
    }

    private void preferenceInitializer() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        this.url = propertiesComponent.getValue(ServerPropertiesManager.URL);
        this.handler = propertiesComponent.getValue(ServerPropertiesManager.HANDLER);
        if(!StringUtils.isNoneBlank(this.url, this.handler)){
            // TODO properties file for default url and handl
            this.url = "http://localhost:1990/confluence";
            this.handler = "livingdoc1";
        }
    }

    public JBTextField getUrlTextField() {
        return this.urlTextField;
    }

    public JBTextField getHandlerTextField() {
        return this.handlerTextField;
    }
}
