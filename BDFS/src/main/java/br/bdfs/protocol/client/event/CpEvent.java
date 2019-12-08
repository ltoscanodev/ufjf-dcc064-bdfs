/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.client.event;

import br.bdfs.event.DfsSendEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidPathException;
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
public class CpEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "CP";
    
    private final String localPath;
    private final String remotePath;
    private final String remoteDataId;
    private final String token;

    public CpEvent(DfsProtocol protocol, String token, String localPath, String remoteDataPath) throws InvalidPathException 
    {
        super(protocol);
        
        this.localPath = localPath;
        this.token = token;
        this.remotePath = remoteDataPath;
        this.remoteDataId = PathHelper.getName(remoteDataPath);
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException {
        LogHelper.logDebug("CpEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("DATA_PATH", remotePath);
        paramList.put("RECEIVE_DATA_PATH", localPath);
        paramList.put("DATA_ID", remoteDataId);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
    
}
