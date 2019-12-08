package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsSendEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidFileNameException;
import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.PathHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class ReadEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "READ";
    
    private final String receiveDataPath;
    private final String dataId;
    
    public ReadEvent(DfsProtocol protocol, String receiveDataPath) throws InvalidFileNameException, InvalidPathException 
    {
        super(protocol);
        
        this.receiveDataPath = receiveDataPath;
        this.dataId = PathHelper.getName(receiveDataPath);
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        LogHelper.logDebug("ReadEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("RECEIVE_DATA_PATH", receiveDataPath);
        paramList.put("DATA_ID", dataId);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
