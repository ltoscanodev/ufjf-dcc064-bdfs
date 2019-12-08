/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.model.controller.helper.DfsDataNodeHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author igor6
 */
public class LeaveEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LEAVE";

    public LeaveEvent(DfsProtocol protocol, Socket clientSocket) {
        super(protocol);
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException {
        LogHelper.logDebug("LeaveEvent.receiveEvent()");
        
        String rmvDataNodeIp = receivedEventMessage.getEventParamList().get("RMV_DATA_NODE_IP");
        String rmvDataNodePort = receivedEventMessage.getEventParamList().get("RMV_DATA_NODE_PORT");
        
        if(!ObjectHelper.strIsNullOrEmpty(rmvDataNodeIp) && !ObjectHelper.strIsNullOrEmpty(rmvDataNodePort))
        {
            DfsDataNodeHelper.disconnectDataNode(rmvDataNodeIp, Integer.valueOf(rmvDataNodePort));
            
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        }
        else
        {
            throw new InvalidMessageException();
        }
        
        return receivedEventMessage;
    }
    
}
