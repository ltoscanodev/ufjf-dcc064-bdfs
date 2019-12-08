package br.bdfs.node.data;

import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.ServerAlreadyRunningException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.data.DfsDataProtocol;
import br.bdfs.protocol.server.DfsServer;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class BDFSDataNode implements Runnable
{
    private final static String DFS_ADDRESS_IP = "127.0.0.1";
    private final static int DFS_ADDRESS_PORT = 6565;
    private final DfsEventService eventService;
    private final DfsDataProtocol dataProtocol;
    private final DfsServer dfsServer;
    
    public BDFSDataNode(DfsAddress localAddress, String storagePath) throws UnknownHostException
    {
        this.eventService = new DfsEventService();
        this.dataProtocol = new DfsDataProtocol(eventService, storagePath);
        this.dfsServer = new DfsServer(eventService, localAddress);
    }
    
    public void start() throws ServerAlreadyRunningException, IOException
    {
        try {
            join(DfsAddress.fromString(DFS_ADDRESS_IP, DFS_ADDRESS_PORT));
        } catch (DfsException ex) {
            LogHelper.logError(
                    String.format("Um erro ocorreu ao tentar conectar o servidor: %s",
                            ex.getMessage()));
        }
        this.dfsServer.start();
    }
    
    public void stop() throws IOException
    {
        try {
            leave(DfsAddress.fromString(DFS_ADDRESS_IP, DFS_ADDRESS_PORT));
        } catch (DfsException ex) {
            LogHelper.logError(
                    String.format("Um erro ocorreu ao tentar desconectar do servidor: %s",
                            ex.getMessage()));
        }
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
    
    public boolean join(DfsAddress dfsAddress) throws DfsException, IOException
    {
        return this.dataProtocol.join(dfsAddress,dfsServer.getLocalAddress());
    }
    
    public void leave(DfsAddress dfsAddress) throws DfsException, IOException
    {
        this.dataProtocol.leave(dfsAddress,dfsServer.getLocalAddress());
    }
}
