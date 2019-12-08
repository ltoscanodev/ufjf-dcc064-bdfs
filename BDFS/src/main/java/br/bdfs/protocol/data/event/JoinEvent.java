/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.data.event;

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
public class JoinEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "JOIN";
    private final DfsAddress homeAddress;

    public JoinEvent(DfsProtocol protocol, DfsAddress homeAddress) {
        super(protocol);
        this.homeAddress = homeAddress;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException {
        LogHelper.logDebug("JoinEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("NEW_DATA_NODE_IP", homeAddress.getIp().getHostAddress());
        paramList.put("NEW_DATA_NODE_PORT", String.valueOf(homeAddress.getPort()));
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
    
}
