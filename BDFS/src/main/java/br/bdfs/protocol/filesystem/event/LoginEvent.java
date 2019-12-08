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
public class LoginEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LOGIN";
    
    private final Socket clientSocket;

    public LoginEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }
    
    @Override
    public DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        LogHelper.logDebug("LoginEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("USERNAME")
                || !receivedEventMessage.getEventParamList().containsKey("PASSWORD"))
        {
            throw new InvalidMessageException();
        }
        
        String username = receivedEventMessage.getEventParamList().get("USERNAME");
        String password = receivedEventMessage.getEventParamList().get("PASSWORD");
        
        String token = DfsUserHelper.login(username, password);

        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("TOKEN", token);
        
        return receivedEventMessage;
    }
}