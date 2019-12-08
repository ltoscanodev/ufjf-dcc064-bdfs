package br.bdfs.event.dispatcher;

import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class DfsEventService 
{
    private final HashMap<String, DfsNotifyEvent> notifyEventMap;
    
    public DfsEventService()
    {
        this.notifyEventMap = new HashMap<>();
    }
    
    public void registerEvent(String eventName, DfsNotifyEvent notify)
    {
        notifyEventMap.put(eventName, notify);
    }
    
    public void unregisterEvent(String eventName)
    {
        notifyEventMap.remove(eventName);
    }
    
    public void notifyEvent(DfsAddress remoteAddress, Socket clientSocket, DfsEventMessage receivedEventMessage) throws DfsException
    {
        if (!notifyEventMap.containsKey(receivedEventMessage.getEventName()))
        {
            LogHelper.logDebug(
                    String.format("A mensagem de evento recebida não é aceita por nenhum protocolo: %s", 
                    receivedEventMessage.toString()));
        }
        else
        {
            notifyEventMap.get(receivedEventMessage.getEventName()).notifyEvent(remoteAddress, clientSocket, receivedEventMessage);
        }
    }
}