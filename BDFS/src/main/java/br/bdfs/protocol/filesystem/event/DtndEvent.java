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
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.controller.helper.DfsDataNodeHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.lang.Integer;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author igor6
 */
public class DtndEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "DTND";
    
    private final Socket clientSocket;

    public DtndEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("DtndEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        
        String newDataNodeIp = receivedEventMessage.getEventParamList().get("NEW_DATA_NODE_IP");
        String newDataNodePort = receivedEventMessage.getEventParamList().get("NEW_DATA_NODE_PORT");
        
        String listDataNodes = receivedEventMessage.getEventParamList().get("LIST_DATA_NODES");
        
        if(!ObjectHelper.strIsNullOrEmpty(newDataNodeIp) && !ObjectHelper.strIsNullOrEmpty(newDataNodePort))
        {
            PingEvent ping = new PingEvent(getProtocol());
            DfsAddress dataNodeAddress = DfsAddress.fromString(newDataNodeIp, Integer.parseInt(newDataNodePort));
            DfsEventMessage aux = ping.sendEvent(dataNodeAddress, true);
            
            if(!"OK".equals(aux.getEventParamList().get("STATUS")))
            {
                throw new NotFoundException("O nó de dados não pode ser 'pingado'.");
            }
            
            DfsDataNodeHelper.createDataNode(token, newDataNodeIp, Integer.parseInt(newDataNodePort));
            
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        }
        else if (!ObjectHelper.strIsNullOrEmpty(listDataNodes))
        {
            List<DfsDataNode> nodes = DfsDataNodeHelper.findDataNode(token);
            
            if(nodes.isEmpty())
            {
                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("STATUS", "Nenhum nó de dados encontrado");
            }
            else
            {
                StringBuilder childBuilder = new StringBuilder();

                for (DfsDataNode child : nodes) {
                    childBuilder.append(child.getAddressIp());
                    childBuilder.append(":");
                    childBuilder.append(String.valueOf(child.getAddressPort()));
                    childBuilder.append(",");
                }

                childBuilder.deleteCharAt(childBuilder.length() - 1);

                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("DATA_NODES", childBuilder.toString());
            }
        
            return receivedEventMessage;
        }
        else
        {
            throw new InvalidMessageException();
        }
        
        return receivedEventMessage;
    }
    
}
