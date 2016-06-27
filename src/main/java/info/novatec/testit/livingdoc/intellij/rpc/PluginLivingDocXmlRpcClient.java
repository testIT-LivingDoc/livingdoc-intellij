package info.novatec.testit.livingdoc.intellij.rpc;

import info.novatec.testit.livingdoc.server.LivingDocServerException;
import info.novatec.testit.livingdoc.server.domain.Project;
import info.novatec.testit.livingdoc.server.domain.SystemUnderTest;
import info.novatec.testit.livingdoc.server.rpc.xmlrpc.LivingDocXmlRpcClient;

import java.util.Set;

/**
 * Created by mruiz on 18/06/2016.
 */
public class PluginLivingDocXmlRpcClient extends LivingDocXmlRpcClient {

    public static String IDENTIFIER = "WORKSPACE";

    public PluginLivingDocXmlRpcClient() {

        super(new ServerPropertiesManagerImpl());
    }
}
