package org.livingdoc.intellij.gui;

import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.border.IdeaTitledBorder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class GuiUtils {

    private GuiUtils() {
        // Utility class.
    }

    /**
     * Returns a title with the following format (e.g): 'title'_____________________
     *
     * @param title String
     * @return {@link IdeaTitledBorder}
     */
    @NotNull
    public static IdeaTitledBorder createTitledBorder(@NotNull final String title) {
        return IdeBorderFactory.createTitledBorder(title, true,
                new Insets(IdeBorderFactory.TITLED_BORDER_TOP_INSET,
                        5,
                        IdeBorderFactory.TITLED_BORDER_BOTTOM_INSET,
                        IdeBorderFactory.TITLED_BORDER_RIGHT_INSET));
    }
}
