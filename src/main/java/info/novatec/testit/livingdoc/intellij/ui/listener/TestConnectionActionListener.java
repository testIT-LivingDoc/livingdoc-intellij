package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBTextField;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tests the server connection with the fields url and handler.
 * @see ActionListener
 *
 * TODO Add loading popup
 */
public class TestConnectionActionListener implements ActionListener {

    private static final Logger LOG = Logger.getInstance(TestConnectionActionListener.class);

    private final JBTextField urlTextField;
    private final JBTextField handlerTextField;

    public TestConnectionActionListener(final JBTextField urlField, final JBTextField handlerField) {
        this.urlTextField = urlField;
        this.handlerTextField = handlerField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            boolean testOk = service.testConnection(urlTextField.getText(), handlerTextField.getText());
            if (testOk) {
                Messages.showInfoMessage(I18nSupport.getValue("server.configuration.button.test.ok"), I18nSupport.getValue("server.configuration.button.test.title"));
            } else {
                Messages.showErrorDialog(I18nSupport.getValue("server.configuration.button.test.ko"), I18nSupport.getValue("server.configuration.button.test.error"));
            }
        } catch (LivingDocServerException ldse) {
            Messages.showErrorDialog(ldse.getMessage(), I18nSupport.getValue("server.configuration.button.test.error"));
            LOG.error(ldse);
        }
    }
}
