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
public class LoginEvent extends  DfsSendEvent
{
    public static final String EVENT_NAME = "LOGIN";
    
    private final String username;
    private final String password;
    
    public LoginEvent(DfsProtocol protocol, String username, String password)
    {
        super(protocol);
        
        this.username = username;
        this.password = password;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        LogHelper.logDebug("LoginEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("USERNAME", username);
        paramList.put("PASSWORD", password);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}