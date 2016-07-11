package info.novatec.testit.livingdoc.intellij.util;

import com.intellij.ui.components.JBLabel;
import info.novatec.testit.livingdoc.intellij.model.LDProject;
import info.novatec.testit.livingdoc.server.domain.Repository;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    /**
     * Inserts a empty {@link JBLabel} in a {@link JPanel} with {@link GridBagLayout}
     * @param jPanel {@link JPanel}
     * @param file Number of file into the grid
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

    /**
     * Validates the LivingDoc user and password configured in IntelliJ to connect with LivingDoc repository.
     * @param ldProject {@link LDProject}
     * @param repository {@link Repository}
     * @return True if the credentials are valid. Otherwise, false.
     */
    public static boolean validateCredentials(final LDProject ldProject, final Repository repository) {

        boolean result = true;

        if(!StringUtils.equals(ldProject.getUser(), repository.getUsername())
                || !StringUtils.equals(ldProject.getPass(), repository.getPassword())) {

            result = false;
        }

        return result;
    }
}
