package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.model.controller.helper.DfsUserHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public class LogoutEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LOGOUT";
    
    private final Socket clientSocket;
    
    public LogoutEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }

    @Override
    public DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        LogHelper.logDebug("LogoutEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        DfsUserHelper.logout(token);
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
