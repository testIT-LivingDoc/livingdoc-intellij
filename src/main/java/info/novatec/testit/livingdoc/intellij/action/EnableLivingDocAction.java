package info.novatec.testit.livingdoc.intellij.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * Created by mruiz on 18/06/2016.
 */
public class EnableLivingDocAction extends AnAction {

    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public EnableLivingDocAction() {
        // Set the menu item name, description and icon.
        super("xEnable LivingDoc for _Project", "xTODO", null);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Messages.showMessageDialog("xHello Tittle", "xHello Information", Messages.getInformationIcon());
    }
}
