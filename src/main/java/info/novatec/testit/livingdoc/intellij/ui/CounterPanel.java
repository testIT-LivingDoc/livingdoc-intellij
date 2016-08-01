package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;

import javax.swing.*;
import java.awt.*;

public class CounterPanel extends JPanel {

    private static final long serialVersionUID = -6060605590173432766L;

    private final JBTextField rightsField = createOutputField();
    private final JBTextField wrongsField = createOutputField();
    private final JBTextField errorsField = createOutputField();
    private int total;

    public CounterPanel() {

        setLayout(new FlowLayout(FlowLayout.CENTER));
        //setBorder(new BottomLineBorder());

        addComponents();
    }

    public void setRightsValue(final int value) {
        rightsField.setText(value + "\\" + total);
    }

    public void setWrongsValue(final int value) {
        wrongsField.setText(String.valueOf(value));
    }

    public void setErrorsValue(final int value) {
        errorsField.setText(String.valueOf(value));
    }

    public void setTotal(final int tot) {
        this.total = tot;
    }

    private JBTextField createOutputField() {
        JBTextField jbTextField = new JBTextField("0", 10);
        jbTextField.setEditable(false);
        jbTextField.setHorizontalAlignment(SwingConstants.LEFT);
        jbTextField.setBackground(Color.LIGHT_GRAY);
        jbTextField.setBorder(BorderFactory.createEmptyBorder());
        return jbTextField;
    }

    private void addComponents() {

        JBLabel rightsLabel = new JBLabel(I18nSupport.getValue("repository.view.field.rights.label"),
                /*Icons.FLAG_RIGHT,*/ SwingConstants.LEFT);
        rightsLabel.setLabelFor(rightsField);
        add(rightsLabel);
        add(rightsField);

        JBLabel wrongsLabel = new JBLabel(I18nSupport.getValue("repository.view.field.wrongs.label"),
                /*Icons.FLAG_WRONG,*/ SwingConstants.LEFT);
        wrongsLabel.setLabelFor(wrongsField);
        add(wrongsLabel);
        add(wrongsField);

        JBLabel errorsLabel = new JBLabel(I18nSupport.getValue("repository.view.field.errors.label",
                /*Icons.FLAG_ERROR,*/ SwingConstants.LEFT));
        add(errorsLabel);
        add(errorsField);
    }
}
