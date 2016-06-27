package info.novatec.testit.livingdoc.intellij.ui.listener;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.Messages;
import info.novatec.testit.livingdoc.intellij.rpc.PluginLivingDocXmlRpcClient;
import info.novatec.testit.livingdoc.intellij.util.I18nSupport;
import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.ServerPropertiesManager;
import info.novatec.testit.livingdoc.server.rpc.RpcClientService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by mruiz on 19/06/2016.
 */
public class TestConnectionActionListener implements ActionListener {

    private String url;
    private String handler;

    /**
     * Constructor
     *
     * @param sUrl
     * @param sHandler
     */
    public TestConnectionActionListener(final String sUrl, final String sHandler) {
        this.url = sUrl;
        this.handler = sHandler;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        RpcClientService service = new PluginLivingDocXmlRpcClient();
        try {
            boolean testOk = service.testConnection(url, handler);
            if (testOk) {

                PropertiesComponent properties = PropertiesComponent.getInstance();
                properties.setValue(ServerPropertiesManager.URL, url);
                properties.setValue(ServerPropertiesManager.HANDLER, handler);

                Messages.showMessageDialog(I18nSupport.i18n_str("server.configuration.button.test.ok"), I18nSupport.i18n_str("server.configuration.button.test.title"), Messages.getInformationIcon());
            } else {
                // TODO Extract string to locale file
                Messages.showMessageDialog("Failed Connection", I18nSupport.i18n_str("server.configuration.button.test.error"), Messages.getErrorIcon());
            }
        } catch (LivingDocServerException ldse) {
            Messages.showMessageDialog(ldse.getMessage(), I18nSupport.i18n_str("server.configuration.button.test.error"), Messages.getErrorIcon());
        }
    }
}
