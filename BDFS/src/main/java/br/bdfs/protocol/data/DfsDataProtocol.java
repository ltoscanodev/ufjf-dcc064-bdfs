package br.bdfs.protocol.data;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import br.bdfs.protocol.data.event.DelEvent;
import br.bdfs.protocol.data.event.JoinEvent;
import br.bdfs.protocol.data.event.LeaveEvent;
import br.bdfs.protocol.data.event.PingEvent;
import br.bdfs.protocol.data.event.ReadEvent;
import br.bdfs.protocol.data.event.WriteEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class DfsDataProtocol extends DfsProtocol
{
    private static final HashMap<String, Class> EVENT_LIST;
    private static final String DFS_ADDRESS_IP = "localhost";
    private static final int DFS_ADDRES_PORT = 6565;
    
    static
    {
        EVENT_LIST = new HashMap<>();
        
        EVENT_LIST.put(ReadEvent.EVENT_NAME, ReadEvent.class);
        EVENT_LIST.put(WriteEvent.EVENT_NAME, WriteEvent.class);
        EVENT_LIST.put(DelEvent.EVENT_NAME, DelEvent.class);
        EVENT_LIST.put(PingEvent.EVENT_NAME, PingEvent.class);
        EVENT_LIST.put(JoinEvent.EVENT_NAME, JoinEvent.class);
        EVENT_LIST.put(LeaveEvent.EVENT_NAME, LeaveEvent.class);
    }
    
    private final String storagePath;
    
    public DfsDataProtocol(DfsEventService eventService, String storagePath) 
    {
        super(EVENT_LIST, eventService);
        
        this.storagePath = storagePath;
        File storage = new File(storagePath);
        
        if(!storage.exists())
        {
            storage.mkdirs();
            LogHelper.logDebug("Diretório de storage criado");
        }
        
    }

    @Override
    public void notifyEvent(DfsAddress remoteAddress, Socket clientSocket, DfsEventMessage receivedEventMessage)
    {
        try 
        {
            Class eventClass = EVENT_LIST.get(receivedEventMessage.getEventName());
            
            Constructor eventClassConstrutor = null;
            DfsReceiveEvent receivedEvent = null;
            
            try
            {
                eventClassConstrutor = eventClass.getConstructor(DfsProtocol.class, String.class, Socket.class);
                receivedEvent = (DfsReceiveEvent)eventClassConstrutor.newInstance(this, storagePath, clientSocket);
            }
            catch(NoSuchMethodException | SecurityException ex)
            {
                try 
                {
                    eventClassConstrutor = eventClass.getConstructor(DfsProtocol.class, String.class);
                    receivedEvent = (DfsReceiveEvent)eventClassConstrutor.newInstance(this, storagePath);
                } 
                catch (NoSuchMethodException | SecurityException ex1) 
                {
                    LogHelper.logError(String.format("Ocorreu um erro durante a instanciação da classe de evento: %s", ex1.getMessage()));
                    
                    try 
                    {
                        receivedEventMessage.getEventParamList().clear();
                        receivedEventMessage.getEventParamList().put("STATUS", ex.getMessage());

                        BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), DfsConfig.MSG_CHARSET));
                        bufferWriter.write(receivedEventMessage.toString());
                        bufferWriter.flush();
                    }
                    catch (IOException ex2) 
                    {
                        LogHelper.logError(String.format("Não foi possível retornar a mensagem de status para %s: %s", remoteAddress.toString(), ex1.getMessage()));
                    }
                }
            }
            
            receivedEvent.receiveEvent(remoteAddress, clientSocket, receivedEventMessage);
        } 
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
                | InvocationTargetException | DfsException | IOException ex) 
        {
            try 
            {
                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("STATUS", ex.getMessage());

                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), DfsConfig.MSG_CHARSET));
                bufferWriter.write(receivedEventMessage.toString());
                bufferWriter.flush();
            } 
            catch (IOException ex1) 
            {
                LogHelper.logError(String.format("Não foi possível retornar a mensagem de status para %s: %s", remoteAddress.toString(), ex1.getMessage()));
            }
        }
    }
    
    public boolean join(DfsAddress dfsAddress, DfsAddress homeAddress) throws DfsException, IOException
    {
        //Instantiate event and send it
        JoinEvent joinEvent = new JoinEvent(this,homeAddress);
        DfsEventMessage responseEventMessage = joinEvent.sendEvent(dfsAddress, true);
        
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            if(!status.equalsIgnoreCase("OK"))
            {
                    throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
            
            LogHelper.logError(String.format("Ingressou no grupo de trabalho de %s", dfsAddress.toString()));
            return true;
        }
        
        throw new DfsException("Mensagem Inválida");
    }
    
    public void leave(DfsAddress dfsAddress, DfsAddress homeAddress) throws DfsException, IOException
    {
        LeaveEvent leaveEvent = new LeaveEvent(this, homeAddress);
        leaveEvent.sendEvent(homeAddress, false);

        LogHelper.logError(String.format("Retirou-se do grupo de trabalho de %s", dfsAddress.toString()));
    }
}
