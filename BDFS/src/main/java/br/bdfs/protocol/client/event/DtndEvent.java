/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.client.event;

import br.bdfs.event.DfsSendEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author igor6
 */
public class DtndEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "DTND";
    
    private final String token;
    private final String[] params;

    public DtndEvent(DfsProtocol protocol, String token, String[] params) {
        super(protocol);
        
        this.token = token;
        this.params = params;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        LogHelper.logDebug("DtndEvent.sendEvent()");
        
        if(params.length == 0)
        {
            throw new DfsException("Parâmetros inválidos");
        }
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        switch (params[0]) 
        {
            case "-N":
                if (params.length != 3) 
                {
                    throw new DfsException("Parâmetros inválidos");
                }
                
                paramList.put("NEW_DATA_NODE_IP", params[1]);
                paramList.put("NEW_DATA_NODE_PORT", params[2]);
                break;
            case "-L":
                paramList.put("LIST_DATA_NODES", "true");
                break;
            default:
                throw new DfsException("Parâmetros inválidos");
        }
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
    
}
