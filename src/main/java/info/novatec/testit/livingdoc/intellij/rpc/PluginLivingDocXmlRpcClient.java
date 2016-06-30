package info.novatec.testit.livingdoc.intellij.rpc;

import info.novatec.testit.livingdoc.server.rpc.xmlrpc.LivingDocXmlRpcClient;

/**
 * Extends to use a {@link info.novatec.testit.livingdoc.server.ServerPropertiesManager} implementation
 * @see LivingDocXmlRpcClient
 * @see ServerPropertiesManagerImpl
 */
public class PluginLivingDocXmlRpcClient extends LivingDocXmlRpcClient {

    public PluginLivingDocXmlRpcClient() {

        super(new ServerPropertiesManagerImpl());
    }
}
