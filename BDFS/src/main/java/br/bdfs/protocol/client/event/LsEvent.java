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
 * @author ltosc
 */
public class LsEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "LS";
    
    private final String token;
    private final String path;
    
    public LsEvent(DfsProtocol protocol, String token, String path) 
    {
        super(protocol);
        
        this.token = token;
        this.path = path;
    }
    
    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        LogHelper.logDebug("LsEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("PATH", path);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}