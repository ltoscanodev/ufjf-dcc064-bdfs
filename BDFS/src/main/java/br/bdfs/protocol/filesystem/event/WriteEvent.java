package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsSendEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidFileNameException;
import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.PathHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author ltosc
 */
public class WriteEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "WRITE";
    
    private final String sendDataPath;
    private final String dataId;
    
    public WriteEvent(DfsProtocol protocol, String sendDataPath) throws InvalidPathException, InvalidFileNameException
    {
        super(protocol);
        
        this.sendDataPath = sendDataPath;
        this.dataId = PathHelper.getName(sendDataPath);
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException
    {
        LogHelper.logDebug("WriteEvent.sendEvent()");
        
        File sendFile = new File(sendDataPath);
        
        if(!sendFile.exists())
        {
            throw new NotFoundException(String.format("Caminho do arquivo %s não encontrado", sendDataPath));
        }
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("SEND_DATA_PATH", sendDataPath);
        paramList.put("DATA_ID", dataId);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
