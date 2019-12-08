package br.bdfs.node.filesystem;

import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.ServerAlreadyRunningException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.filesystem.DfsFileSystemProtocol;
import br.bdfs.protocol.server.DfsServer;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class BDFSFileSystemNode implements Runnable
{
    private final DfsEventService eventService;
    private final DfsFileSystemProtocol fileSystemProtocol;
    private final DfsServer dfsServer;
    
    public BDFSFileSystemNode(DfsAddress localAddress, String cachePath)
    {
        this.eventService = new DfsEventService();
        this.fileSystemProtocol = new DfsFileSystemProtocol(eventService, cachePath);
        this.dfsServer = new DfsServer(eventService, localAddress);
    }
    
    public void start() throws ServerAlreadyRunningException, IOException
    {
        this.dfsServer.start();
    }
    
    public void stop() throws IOException
    {
        this.dfsServer.stop();
    }
    
    @Override
    public void run() 
    {
        try
        {
            start();
        } 
        catch (ServerAlreadyRunningException | IOException ex) 
        {
            LogHelper.logError(
                    String.format("Um erro ocorreu ao iniciar o servidor: %s",
                            ex.getMessage()));
        }
    }

    public DfsAddress getLocalAddress() 
    {
        return this.dfsServer.getLocalAddress();
    }

    public boolean isRunning() 
    {
        return this.dfsServer.isRunning();
    }
}
