package info.novatec.testit.livingdoc.intellij.ui;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;


public class UIUtilsTest {

    @Test
    public void validateCredentials() {

        JPanel jPanel = new JPanel();
        UIUtils.insertSpace(jPanel, 2, 2);

        Component component = jPanel.getComponent(0);
        Assert.assertNotNull(component);
    }

}