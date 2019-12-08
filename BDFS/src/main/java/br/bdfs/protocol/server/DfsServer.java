package br.bdfs.protocol.server;

import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.ServerAlreadyRunningException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.SocketHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.config.DfsConfig;
import br.bdfs.event.dispatcher.DfsEventDispatcher;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ltosc
 */
public class DfsServer
{
    private final DfsEventService eventService;
    private final DfsAddress localAddress;
    private final ThreadPoolExecutor clientThreadPool;
    private ServerSocket serverSocket;
    private boolean running;
    
    public DfsServer(DfsEventService eventService, DfsAddress localAddress)
    {
        this.eventService = eventService;
        this.localAddress = localAddress;
        
        this.clientThreadPool = new ThreadPoolExecutor(
                DfsConfig.POOL_MIN_THREAD, 
                DfsConfig.POOL_MAX_THREAD, 
                DfsConfig.POOL_KEEP_TO_ALIVE_THREAD, TimeUnit.SECONDS, 
                new LinkedBlockingQueue<>());
        
        this.serverSocket = null;
        this.running = false;
    }
    
    public void start() throws ServerAlreadyRunningException, IOException
    {
        if(running)
        {
            throw new ServerAlreadyRunningException();
        }
        
        if(br.bdfs.config.DfsConfig.USE_SSL_SOCKET)
        {
            try
            {
                serverSocket = SocketHelper.createSSLServerSocket(localAddress);
            } 
            catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) 
            {
                LogHelper.logError(String.format("Erro ao iniciar o socket do servidor: %s", ex.getMessage()));
                return;
            }
        }
        else
        {
            serverSocket = SocketHelper.createServerSocket(localAddress);
        }
        
        running = true;
        
        LogHelper.logInfo("O servidor foi iniciado");
        
        LogHelper.logDebug(String.format("IP: %s | Porta: %s", 
                getLocalAddress().getIp().getHostAddress(),
                getLocalAddress().getPort()));
        
        LogHelper.logInfo("Esperando por conexões...");
        
        try 
        {
            do 
            {
                Socket clientSocket = serverSocket.accept();

                LogHelper.logInfo(String.format("Nova conexão: %s:%s",
                        clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort()));

                clientThreadPool.execute(new DfsEventDispatcher(clientSocket, eventService));
            } 
            while (isRunning());
        }
        catch (SocketException ex) 
        {
            LogHelper.logError(String.format("Espera por conexões interrompida: %s", ex.getMessage()));
        } 
    }
    
    public void stop() throws IOException
    {
        if(isRunning())
        {
            serverSocket.close();
            running = false;

            LogHelper.logInfo("O servidor parou");
        }
    }

    /**
     * @return the localAddress
     */
    public DfsAddress getLocalAddress() {
        return localAddress;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }
}
