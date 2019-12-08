package br.bdfs;

import br.bdfs.node.filesystem.BDFSFileSystemNode;
import br.bdfs.config.DfsConfig;
import br.bdfs.context.AppContext;
import br.bdfs.exceptions.ServerAlreadyRunningException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class FileSystemNodeTest
{
    public static void main(String[] args)
    {
        try
        {
            AppContext.initialize();
            DfsConfig.setDebug(true);
            
            String cachePath = "D:\\DFS\\FSNode";
            
            BDFSFileSystemNode bdfsFS = new BDFSFileSystemNode(DfsAddress.fromString("localhost", 6565), cachePath);
            bdfsFS.start();
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
