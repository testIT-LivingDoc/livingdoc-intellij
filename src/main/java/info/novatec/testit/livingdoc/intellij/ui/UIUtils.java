package info.novatec.testit.livingdoc.intellij.ui;

import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    private UIUtils() {
        // Utility class.
    }

    /**
     * Inserts a empty {@link JBLabel} in a {@link JPanel} with {@link GridBagLayout}
     *
     * @param jPanel       {@link JPanel}
     * @param file         Number of file into the grid
     * @param totalColumns Grid total columns
     * @see GridBagLayout
     */
    public static void insertSpace(final JPanel jPanel, final int file, final int totalColumns) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = file;
        constraints.gridwidth = totalColumns;
        constraints.gridheight = 1;
        jPanel.add(new JBLabel("\n"), constraints);
    }
}
