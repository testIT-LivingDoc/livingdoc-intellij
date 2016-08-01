package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.ui.listener.TestConnectionActionListener;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.intellij.util.PluginProperties;
import info.novatec.testit.livingdoc.intellij.util.UIUtils;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * User Interface for LivingDoc server configuration.
 * The dialog's layout is a {@link GridBagLayout}.
 * The default fields values are configured in {@link PluginProperties}
 *
 * @see DialogWrapper
 */
public class ServerConfigurationUI extends DialogWrapper {

    private JPanel jPanel;
    private String url;
    private String handler;
    private JBTextField urlTextField;
    private JBTextField handlerTextField;
    private JButton testButton;

    public ServerConfigurationUI(Project project) {

        super(project);

        this.preferenceInitializer();

        this.addComponents();
        this.addListeners();

        init();
    }

    public JBTextField getUrlTextField() {
        return this.urlTextField;
    }

    public JBTextField getHandlerTextField() {
        return this.handlerTextField;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return jPanel;
    }

    private void preferenceInitializer() {

        setTitle(I18nSupport.getValue("server.configuration.header"));

        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        this.url = propertiesComponent.getValue(ServerPropertiesManager.URL);
        this.handler = propertiesComponent.getValue(ServerPropertiesManager.HANDLER);

        if (StringUtils.isBlank(this.url)) {
            this.url = PluginProperties.getValue("livingdoc.url.default");
        }
        if (StringUtils.isBlank(this.handler)) {
            this.handler = PluginProperties.getValue("livingdoc.handler.default");
        }
    }

    private void addListeners() {
        this.testButton.addActionListener(new TestConnectionActionListener(urlTextField, handlerTextField));
    }

    private void addComponents() {

        jPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        JBLabel titleLabel = new JBLabel(I18nSupport.getValue("server.configuration.title"));
        titleLabel.setForeground(new JBColor(JBColor.BLACK, JBColor.ORANGE));
        titleLabel.setFont(new Font(null, Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.WEST;
        jPanel.add(titleLabel, constraints);
        UIUtils.insertSpace(jPanel, 1, 2);

        JBLabel infoLabel = new JBLabel(I18nSupport.getValue("server.configuration.info"));
        constraints.gridy = 2;
        jPanel.add(infoLabel, constraints);

        infoLabel = new JBLabel(I18nSupport.getValue("server.configuration.info.1"));
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        jPanel.add(infoLabel, constraints);

        infoLabel = new JBLabel(I18nSupport.getValue("server.configuration.info.2"));
        constraints.gridy = 4;
        jPanel.add(infoLabel, constraints);
        UIUtils.insertSpace(jPanel, 5, 2);

        JBLabel urlLabel = new JBLabel(I18nSupport.getValue("server.configuration.field.url.label"));
        urlLabel.setLabelFor(urlTextField);
        constraints.gridy = 6;
        constraints.gridwidth = 1;
        jPanel.add(urlLabel, constraints);

        urlTextField = new JBTextField();
        urlTextField.setName(ServerPropertiesManager.URL);
        urlTextField.setText(this.url);
        urlTextField.setColumns(20);
        constraints.gridx = 1;
        constraints.gridy = 6;
        jPanel.add(urlTextField, constraints);

        JBLabel handlerLabel = new JBLabel(I18nSupport.getValue("server.configuration.field.handler.label"));
        handlerLabel.setLabelFor(handlerTextField);
        constraints.gridx = 0;
        constraints.gridy = 7;
        jPanel.add(handlerLabel, constraints);

        handlerTextField = new JBTextField();
        handlerTextField.setName(ServerPropertiesManager.HANDLER);
        handlerTextField.setText(this.handler);
        handlerTextField.setColumns(20);
        constraints.gridx = 1;
        constraints.gridy = 7;
        jPanel.add(handlerTextField, constraints);
        UIUtils.insertSpace(jPanel, 8, 2);

        testButton = new JButton();
        testButton.setText(I18nSupport.getValue("server.configuration.button.test.label"));
        constraints.gridy = 9;
        constraints.anchor = GridBagConstraints.EAST;
        jPanel.add(testButton, constraints);
        UIUtils.insertSpace(jPanel, 10, 2);
    }
}
