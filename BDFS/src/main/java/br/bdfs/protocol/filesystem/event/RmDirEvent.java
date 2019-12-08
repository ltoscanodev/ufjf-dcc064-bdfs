package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public class RmDirEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "RMDIR";
    
    private final Socket clientSocket;
    
    public RmDirEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }
    
    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("RmDirEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN") 
                || !receivedEventMessage.getEventParamList().containsKey("PATH"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        
        boolean status = DfsDirectoryHelper.deleteUserDirectory(token, path);
        
        if(status)
        {
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        }
        else
        {
            throw new DfsException("Caminho não encontrado");
        }
        
        return receivedEventMessage;
    }
}
