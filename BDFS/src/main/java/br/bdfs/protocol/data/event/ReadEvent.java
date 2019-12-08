package br.bdfs.protocol.data.event;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public class ReadEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "READ";
    
    private final String storagePath;
    private final Socket clientSocket;
    private final DfsAddress clientAddress;

    public ReadEvent(DfsProtocol protocol, String storagePath, Socket clientSocket) 
    {
        super(protocol);
        this.storagePath = storagePath + File.separator;
        
        this.clientSocket = clientSocket;
        this.clientAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        LogHelper.logDebug("ReadEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("DATA_ID"))
        {
            throw new InvalidMessageException();
        }
        
        String dataId = receivedEventMessage.getEventParamList().get("DATA_ID");
        String filePath = String.format("%s%s", storagePath, dataId);
        
        File readFile = new File(filePath);
        
        if(!readFile.exists())
        {
            throw new DfsException("Arquivo não existe");
        }
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("DATA_LENGTH", String.valueOf(readFile.length()));
        
        BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), DfsConfig.MSG_CHARSET));
        bufferWriter.write(receivedEventMessage.toString());
        bufferWriter.flush();
        
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());

        byte[] readBuffer = new byte[DfsConfig.FILE_RW_BUFFER_LENGTH];
        long dataLength = readFile.length();
        long totalRead = 0;
        long read;

        LogHelper.logDebug(String.format("Enviando dados para %s...", clientAddress.toString()));

        try (FileInputStream fileInputStream = new FileInputStream(readFile)) 
        {
            do 
            {
                read = Math.min((dataLength - totalRead), DfsConfig.FILE_RW_BUFFER_LENGTH);
                fileInputStream.read(readBuffer, 0, Math.toIntExact(read));
                totalRead += read;

                bufferedOutputStream.write(readBuffer, 0, Math.toIntExact(read));
            } while (totalRead < dataLength);

            bufferedOutputStream.flush();
        }

        LogHelper.logDebug(String.format("Dados enviados para %s", clientAddress.toString()));
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
}
