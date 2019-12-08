package br.bdfs;

import br.bdfs.config.DfsConfig;
import br.bdfs.context.AppContext;
import br.bdfs.exceptions.ServerAlreadyRunningException;
import br.bdfs.helper.LogHelper;
import br.bdfs.node.data.BDFSDataNode;
import br.bdfs.protocol.DfsAddress;
import java.io.IOException;
import org.springframework.util.SocketUtils;

/**
 *
 * @author ltosc
 */
public class DataNodeTest
{
    public static void main(String[] args)
    {
        try
        {
            AppContext.initialize();
            DfsConfig.setDebug(true);
            int port = SocketUtils.findAvailableTcpPort();
            
            String storagePath = "D:\\DFS\\DataNode\\" + port;
            
            BDFSDataNode bdfsDataNode = new BDFSDataNode(DfsAddress.fromString("localhost", port), storagePath);
            bdfsDataNode.start();
        } 
        catch (ServerAlreadyRunningException | IOException ex) 
        {
            LogHelper.logError(ex.getMessage());
        }
        finally
        {
            AppContext.close();
        }
    }
}
