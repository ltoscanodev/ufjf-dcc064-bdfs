package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.model.controller.helper.DfsUserHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public class CdEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "CD";
    
    private final Socket clientSocket;
    
    public CdEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }
    
    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("CdEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN") 
                || !receivedEventMessage.getEventParamList().containsKey("PATH"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        
        DfsDirectory dir;

        if (path.equals("~")) 
        {
            dir = DfsUserHelper.getUserHomeDirectory(token);
        }
        else 
        {
            dir = DfsDirectoryHelper.findUserDirectory(token, path);
        }

        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("PATH", dir.getPath());
        
        return receivedEventMessage;
    }
}
