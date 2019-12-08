package br.bdfs.protocol.client.event;

import br.bdfs.event.DfsSendEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.LogHelper;
import br.bdfs.node.filesystem.DfsPath;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class SdEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "SD";
    
    private final String token;
    private final String[] params;
    
    public SdEvent(DfsProtocol protocol, String token, String[] params) 
    {
        super(protocol);
        
        this.token = token;
        this.params = params;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        LogHelper.logDebug("SdEvent.sendEvent()");
        
        if(params.length == 0)
        {
            throw new DfsException("Parâmetros inválidos");
        }
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        
        switch (params[0]) 
        {
            case "-N":
                if (params.length != 2) 
                {
                    throw new DfsException("Parâmetros inválidos");
                }
                
                paramList.put("NEW_SHARED_DIR", params[1]);
                break;
            case "-U":
                if (params.length != 3) 
                {
                    throw new DfsException("Parâmetros inválidos");
                }
                
                paramList.put("ADD_USER_IN_SHARED_DIR", params[1]);
                paramList.put("SHARED_DIR", new DfsPath(params[2]).toString());
                break;
            case "-L":
                paramList.put("LIST_SHARED_DIR", "true");
                break;
            default:
                throw new DfsException("Parâmetros inválidos");
        }
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
