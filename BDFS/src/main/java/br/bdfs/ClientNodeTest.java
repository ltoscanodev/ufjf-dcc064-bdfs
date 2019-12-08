package br.bdfs;

import br.bdfs.node.client.DfsClientConsole;
import br.bdfs.config.DfsConfig;
import br.bdfs.context.AppContext;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import java.net.UnknownHostException;

/**
 *
 * @author ltosc
 */
public class ClientNodeTest
{
    public static void main(String[] args)
    {
        try
        {
            AppContext.initialize();
            DfsConfig.setDebug(true);
            
            DfsClientConsole clientConsole = new DfsClientConsole(DfsAddress.fromString("localhost", 6565));
            clientConsole.start();
        }
        catch (UnknownHostException ex) 
        {
            LogHelper.logError(ex.getMessage());
        }
        finally
        {
            AppContext.close();
        }
    }
}
