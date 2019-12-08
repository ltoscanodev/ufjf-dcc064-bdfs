/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.data.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author igor6
 */
public class PingEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "PING";
    
    private final DfsAddress clientAddress;
    
    public PingEvent(DfsProtocol protocol, String storagePath, Socket clientSocket) {
        super(protocol);
        clientAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("ReadEvent.receiveEvent()");
        
        LogHelper.logDebug(String.format("Ping retornado para %s", clientAddress.toString()));
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
    
}
