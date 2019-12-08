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
public class RmEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "RM";
    
    private final String token;
    private final String path;

    public RmEvent(DfsProtocol protocol, String token, String path) {
        super(protocol);
        
        this.token = token;
        this.path = path;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse)  throws DfsException, IOException 
    {
        LogHelper.logDebug("RmEvent.sendEvent()");
        
        if(path.isEmpty())
        {
            throw new DfsException("Parâmetros inválidos");
        }
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("FILEPATH", path);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
    
}
