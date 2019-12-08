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
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author igor6
 */
public class MvEvent extends DfsSendEvent
{
    public static final String EVENT_NAME = "MV";
    
    private final String orgnFilePath;
    private final String dstnPath;
    private final String dataId;
    private final String token;

    public MvEvent(DfsProtocol protocol, String token, String orgnFilePath, String dstnPath) throws InvalidPathException {
        super(protocol);
        
        this.orgnFilePath = orgnFilePath;
        this.dataId = PathHelper.getName(orgnFilePath);
        this.token = token;
        this.dstnPath = dstnPath;
    }

    @Override
    public DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException {
        LogHelper.logDebug("MvEvent.sendEvent()");
        
        HashMap<String, String> paramList = new HashMap<>();
        paramList.put("TOKEN", token);
        paramList.put("DATA_PATH", orgnFilePath);
        paramList.put("DATA_ID", dataId);
        paramList.put("DEST_PATH", dstnPath);
        
        return sendMessage(remoteAddress, new DfsEventMessage(EVENT_NAME, paramList), waitForResponse);
    }
}
