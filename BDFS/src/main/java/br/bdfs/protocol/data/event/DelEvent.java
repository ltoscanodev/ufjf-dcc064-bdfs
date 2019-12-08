package br.bdfs.protocol.data.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class DelEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "DEL";
    
    private final String storagePath;
    
    public DelEvent(DfsProtocol protocol, String storagePath) 
    {
        super(protocol);
        this.storagePath = storagePath + File.separator;
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("DelEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("DATA_ID"))
        {
            throw new InvalidMessageException();
        }
        
        String dataId = receivedEventMessage.getEventParamList().get("DATA_ID");
        String filePath = String.format("%s%s", storagePath, dataId);
        
        File delFile = new File(filePath);
        
        if(!delFile.exists())
        {
            throw new DfsException("Arquivo não existe");
        }
        
        boolean status = delFile.delete();
        
        if(status)
        {
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
            
            return receivedEventMessage;
        }
        else
        {
            throw new DfsException("Não foi possível deletar o arquivo");
        }
    }
}
