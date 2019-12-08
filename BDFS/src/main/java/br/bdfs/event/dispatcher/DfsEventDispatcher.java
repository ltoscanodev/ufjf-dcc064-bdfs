package br.bdfs.event.dispatcher;

import br.bdfs.protocol.DfsAddress;
import br.bdfs.config.DfsConfig;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author ltosc
 */
public class DfsEventDispatcher implements Runnable
{
    private final Socket clientSocket;
    private final DfsAddress clientAddress;
    private final DfsEventService eventService;
    
    public DfsEventDispatcher(Socket clientSocket, DfsEventService eventService)
    {
        this.clientSocket = clientSocket;
        this.clientAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
        this.eventService = eventService;
    }
    
    @Override
    public void run() 
    {
        DfsEventMessage receivedEventMessage = null;
        
        try (BufferedReader bufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), DfsConfig.MSG_CHARSET))) 
        {
            clientSocket.setSoTimeout(DfsConfig.SOCKET_TIMEOUT);
            receivedEventMessage = DfsEventMessage.fromString(bufferReader.readLine());
            eventService.notifyEvent(clientAddress, clientSocket, receivedEventMessage);
        }
        catch(SocketTimeoutException ex)
        {
            LogHelper.logError(String.format("Nenhuma mensagem de %s foi recebida no tempo esperado", clientAddress.toString()));
        } 
        catch (DfsException | IOException ex) 
        {
            LogHelper.logError(String.format("Ocorreu um erro durante a notificação de evento para %s", clientAddress.toString()));
        }
        finally
        {
            try 
            {
                clientSocket.close();
                LogHelper.logInfo(String.format("Conexão com %s encerrada", clientAddress.toString()));
            } 
            catch (IOException ex) 
            {
                LogHelper.logError(String.format("Ocorreu um erro ao fechar a conexão com %s: %s", clientAddress.toString(), ex.getMessage()));
            }
        }
    }
}