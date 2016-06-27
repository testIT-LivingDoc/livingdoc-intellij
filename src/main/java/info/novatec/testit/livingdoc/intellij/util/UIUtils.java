package info.novatec.testit.livingdoc.intellij.util;

import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mruiz on 19/06/2016.
 */
public class UIUtils {

    /**
     * Insert a empty label in the jPanel parameter.
     * @param jPanel
     * @param file
     * @param totalColumns
     */
    public static void insertSpace(final JPanel jPanel, final int file, final int totalColumns) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; // column
        constraints.gridy = file; // file
        constraints.gridwidth = totalColumns; // columns
        constraints.gridheight = 1; // rows
        jPanel.add(new JBLabel("\n"), constraints);
    }
}
