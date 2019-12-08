package br.bdfs.protocol.data.event;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public class WriteEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "WRITE";

    private final String storagePath;
    private final Socket clientSocket;
    private final DfsAddress clientAddress;
    
    public WriteEvent(DfsProtocol protocol, String storagePath, Socket clientSocket) 
    {
        super(protocol);
        
        this.storagePath = storagePath + File.separator;
        this.clientSocket = clientSocket;
        this.clientAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("WriteEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("DATA_ID")
                || !receivedEventMessage.getEventParamList().containsKey("DATA_LENGTH"))
        {
            throw new InvalidMessageException();
        }
        
        String dataId = receivedEventMessage.getEventParamList().get("DATA_ID");
        String strDataLength = receivedEventMessage.getEventParamList().get("DATA_LENGTH");
        
        String dataPath = String.format("%s%s", storagePath, dataId);
        
        BufferedInputStream bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
        
        byte[] readBuffer = new byte[DfsConfig.FILE_RW_BUFFER_LENGTH];
        long dataLength = Long.valueOf(strDataLength);
        long totalRead = 0;
        long read;
        
        LogHelper.logDebug(String.format("Recebendo dados de %s...", clientAddress.toString()));
        
        try(FileOutputStream fileOutputStream = new FileOutputStream(new File(dataPath)))
        {
            do
            {
                read = Math.min((dataLength - totalRead), DfsConfig.FILE_RW_BUFFER_LENGTH);
                bufferedInputStream.read(readBuffer, 0, Math.toIntExact(read));
                totalRead += read;
                
                fileOutputStream.write(readBuffer, 0, Math.toIntExact(read));
            }
            while(totalRead < dataLength);
            
            fileOutputStream.flush();
        }

        LogHelper.logDebug(String.format("Dados recebidos de %s", clientAddress.toString()));
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
