/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.client.event;

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
 * @author igor6
 */
public class CreateEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "CREATE";
    
    private final String sendDataPath;
    private final String remotePath;
    private final String dataId;
    private final String token;

    public CreateEvent(DfsProtocol protocol, String token, String sendDataPath, String remotePath) throws InvalidPathException, InvalidFileNameException
    {
        super(protocol);
        
        this.sendDataPath = sendDataPath;
        this.dataId = PathHelper.getName(sendDataPath);
        this.token = token;
        this.remotePath = remotePath;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException 
    {
        LogHelper.logDebug("CreateEvent.sendEvent()");
        
        File sendFile = new File(sendDataPath);
        
        if(!sendFile.exists())
        {
            throw new NotFoundException(String.format("Caminho do arquivo %s não encontrado", sendDataPath));
        }
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("SEND_DATA_PATH", sendDataPath);
        paramList.put("DATA_ID", dataId);
        paramList.put("PATH", remotePath);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
    
}
