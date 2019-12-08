package br.bdfs.protocol.filesystem.event;

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
public class DelEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "DEL";
    
    private final String dataId;
    
    public DelEvent(DfsProtocol protocol, String dataId)
    {
        super(protocol);
        
        this.dataId = dataId;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        LogHelper.logDebug("DelEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("DATA_ID", dataId);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
