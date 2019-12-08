package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.model.DfsSharedDirectory;
import br.bdfs.model.controller.helper.DfsSharedDirectoryHelper;
import br.bdfs.node.filesystem.DfsPath;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class SdEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "SD";
    
    private final Socket clientSocket;
    
    public SdEvent(DfsProtocol protocol, Socket clientSocket)
    {
        super(protocol);
        
        this.clientSocket = clientSocket;
        
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("SdEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        
        String newSharedDir = receivedEventMessage.getEventParamList().get("NEW_SHARED_DIR");
        
        String addUserInSharedDir = receivedEventMessage.getEventParamList().get("ADD_USER_IN_SHARED_DIR");
        String sharedDir = receivedEventMessage.getEventParamList().get("SHARED_DIR");
        
        String listSharedDir = receivedEventMessage.getEventParamList().get("LIST_SHARED_DIR");
        
        if(!ObjectHelper.strIsNullOrEmpty(newSharedDir))
        {
            DfsSharedDirectoryHelper.createSharedDirectory(token, new DfsPath(newSharedDir));
        
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        }
        else if(!ObjectHelper.strIsNullOrEmpty(addUserInSharedDir) && !ObjectHelper.strIsNullOrEmpty(sharedDir))
        {
            DfsSharedDirectoryHelper.addUserInSharedDirectory(token, addUserInSharedDir, sharedDir);
            
            receivedEventMessage.getEventParamList().clear();
            receivedEventMessage.getEventParamList().put("STATUS", "OK");
        }
        else if(!ObjectHelper.strIsNullOrEmpty(listSharedDir))
        {
            List<DfsSharedDirectory> sharedDirList = DfsSharedDirectoryHelper.getSharedDirectories(token);
            
            if (sharedDirList.isEmpty()) 
            {
                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("STATUS", "Nenhum diretório compartilhado");
            } 
            else 
            {
                StringBuilder sharedBuilder = new StringBuilder();

                for (DfsSharedDirectory dir : sharedDirList) {
                    sharedBuilder.append(dir.getSharedDirectory().getPath());
                    sharedBuilder.append(",");
                }

                sharedBuilder.deleteCharAt(sharedBuilder.length() - 1);

                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("SHARED_DIR", sharedBuilder.toString());
            }
        }
        else
        {
            throw new InvalidMessageException();
        }
        
        return receivedEventMessage;
    }
}
