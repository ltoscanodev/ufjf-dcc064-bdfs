package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.DfsFile;
import br.bdfs.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.model.controller.helper.DfsFileHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class LsEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "LS";
    
    private final Socket clientSocket;
    
    public LsEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }

    @Override
    public DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("LsEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN")
                    || !receivedEventMessage.getEventParamList().containsKey("PATH"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String path = receivedEventMessage.getEventParamList().get("PATH");
        
        List<DfsDirectory> childList = DfsDirectoryHelper.getUserDirectories(token, path);
        List<DfsFile> fileList = DfsFileHelper.getUserFiles(token,path);
        
        if(childList.isEmpty() && fileList.isEmpty())
        {
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "Nenhum diretório ou arquivo");
        }
        else
        {
            StringBuilder childBuilder = new StringBuilder();

            for (DfsDirectory child : childList) {
                childBuilder.append(child.getPath());
                childBuilder.append(",");
            }
            
            for (DfsFile child: fileList){
                childBuilder.append(child.getPath());
                childBuilder.append(",");
            }

            childBuilder.deleteCharAt(childBuilder.length() - 1);
            
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("DIR", childBuilder.toString());
        }
        
        return receivedEventMessage;
    }
}
