package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.node.filesystem.DfsPath;
import br.bdfs.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public class MkDirEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "MKDIR";
    
    private final Socket clientSocket;
    
    public MkDirEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }

    @Override
    public DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        LogHelper.logDebug("MkDirEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN")
                || !receivedEventMessage.getEventParamList().containsKey("PATH")
                || !receivedEventMessage.getEventParamList().containsKey("RECURSIVELY"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        boolean recursively = Boolean.valueOf(receivedEventMessage.getEventParamList().get("RECURSIVELY"));
        
        DfsDirectoryHelper.createUserDirectory(token, new DfsPath(path), recursively);
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}