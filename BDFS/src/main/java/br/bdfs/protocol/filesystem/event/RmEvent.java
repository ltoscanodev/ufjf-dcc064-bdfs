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
import br.bdfs.helper.LogHelper;
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.DfsFileFragment;
import br.bdfs.model.controller.helper.DfsFileFragmentHelper;
import br.bdfs.model.controller.helper.DfsFileHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author igor6
 */
public class RmEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "RM";
    
    private final Socket clientSocket;

    public RmEvent(DfsProtocol protocol, Socket clientSocket) {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException {
        
        LogHelper.logDebug("RmDirEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN") 
                || !receivedEventMessage.getEventParamList().containsKey("FILEPATH"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("FILEPATH");
                
        //DISPATCHES DELETION FOR EVERY FRAGMENT IN ITS REPESTIVE(S) DATA NODES
        List<DfsFileFragment> fragments = DfsFileFragmentHelper.getFileFragments(token, path);
        for(DfsFileFragment frag : fragments)
        {
            List<DfsDataNode> dataNodes = frag.getDfsDataNodeList();
            for(DfsDataNode dataNode : dataNodes)
            {
                DelEvent delEvent = new DelEvent(getProtocol(), frag.getGuid());
                DfsAddress auxAddress = DfsAddress.fromString(dataNode.getAddressIp(), dataNode.getAddressPort());
                DfsEventMessage responseEventMessage = delEvent.sendEvent(auxAddress, true);
                
                if(responseEventMessage.getEventParamList().containsKey("STATUS"))
                {
                    String txt = responseEventMessage.getEventParamList().get("STATUS");
                    if(!txt.equalsIgnoreCase("OK"))
                    {
                        throw new DfsException("ERRO CRÍTICO: FRAGMENTO NÃO ENCONTRADO");
                    }
                } else {
                    throw new DfsException("Mensagem não reconhecida");
                }
            }
        }
        
        //DELETES FROM THE DATABASE (BOTH FRAGMENTS AND FILE)s
        boolean status = DfsFileFragmentHelper.deleteFileUser(token, path);
        status = status && DfsFileHelper.deleteUserFile(token, path);
        
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
