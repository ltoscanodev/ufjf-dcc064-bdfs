/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.helper.LogHelper;
import br.bdfs.model.DfsDirectory;
import br.bdfs.model.DfsFile;
import br.bdfs.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.model.controller.helper.DfsFileHelper;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author igor6
 */
public class MvEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "MV";
    
    private final Socket clientSocket;

    public MvEvent(DfsProtocol protocol, Socket clientSocket) {
        super(protocol);
        
        this.clientSocket = clientSocket;
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException {
        LogHelper.logDebug("MvEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("TOKEN")
                || !receivedEventMessage.getEventParamList().containsKey("DATA_PATH")
                || !receivedEventMessage.getEventParamList().containsKey("DATA_ID")
                || !receivedEventMessage.getEventParamList().containsKey("DEST_PATH"))
        {
            throw new InvalidMessageException();
        }
        
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String dataPath = receivedEventMessage.getEventParamList().get("DATA_PATH");
        String dataId = receivedEventMessage.getEventParamList().get("DATA_ID");
        String destPath = receivedEventMessage.getEventParamList().get("DEST_PATH");
        
        DfsFile dfsFile = DfsFileHelper.getUserFile(token, dataPath);
        
        if(dfsFile == null)
        {
            throw new NotFoundException("Arquivo não encontrado.");
        }
        
        DfsDirectory dfsDirectory = DfsDirectoryHelper.findUserDirectory(token, destPath);
        
        if(dfsDirectory == null)
        {
            throw new NotFoundException("Diretório não encontrado.");
        }
        
        dfsFile.setDirectory(dfsDirectory);
        dfsFile.setPath(String.format("%s/%s.%s", dfsDirectory.getPath(),dfsFile.getName(),dfsFile.getExtension()));
        
        DfsFileHelper.updateFile(dfsFile);
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
