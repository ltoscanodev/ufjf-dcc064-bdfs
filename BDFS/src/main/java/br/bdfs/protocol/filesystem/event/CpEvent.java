/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.bdfs.protocol.filesystem.event;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.helper.FileSplitter;
import br.bdfs.helper.LogHelper;
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.DfsFileFragment;
import br.bdfs.model.controller.helper.DfsFileFragmentHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author igor6
 */
public class CpEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "CP";
    public static final String STORAGE_PATH = "D:\\DFS\\FSNode";
    public static final int DATA_SHARDS = 10;
    public static final int PARITY_SHARDS = 22;
    
    private final String storagePath;
    private final Socket clientSocket;
    private final DfsAddress clientAddress;
//    private final ReedSolomonDecoder reedSolomonDecoder;

    public CpEvent(DfsProtocol protocol, Socket clientSocket) {
        super(protocol);
        
        this.storagePath = STORAGE_PATH + File.separator;
        this.clientSocket = clientSocket;
        this.clientAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
//        reedSolomonDecoder = new ReedSolomonDecoder(DATA_SHARDS, PARITY_SHARDS);
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException {
        LogHelper.logDebug("CpEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("DATA_ID")
            || !receivedEventMessage.getEventParamList().containsKey("DATA_PATH")
            || !receivedEventMessage.getEventParamList().containsKey("TOKEN"))
        {
            throw new InvalidMessageException();
        }
        
        String dataId = receivedEventMessage.getEventParamList().get("DATA_ID");
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        String dataPath = receivedEventMessage.getEventParamList().get("DATA_PATH");
        
        String filePath = storagePath + dataId;
        
        //GETS FILE FRAGMENTS
        List<DfsFileFragment> fragments = null;
        try {
            fragments = DfsFileFragmentHelper.getFileFragments(token, dataPath);
        } catch (Exception ex) {
            throw new NotFoundException("O arquivo não foi encontrado.");
        }
        List<DfsFileFragment> fetchFrags = new ArrayList<>();
        List<String> fetchFragsPath = new ArrayList<>();
        
        for(int i = (int) (Math.random() * fragments.size()); fetchFrags.size() < (DATA_SHARDS + PARITY_SHARDS) ; i = (int) (Math.random() * fragments.size()))
        {
            if(!fetchFrags.contains(fragments.get(i)))
            {
                fetchFrags.add(fragments.get(i));
                fetchFragsPath.add(storagePath + fragments.get(i).getGuid());
            }
        }
        
        for(DfsFileFragment f : fetchFrags)
        {
            ReadEvent readEvent = new ReadEvent(getProtocol(), storagePath + f.getGuid());
            List<DfsDataNode> auxDataNode = f.getDfsDataNodeList();
            DfsDataNode d = auxDataNode.get(auxDataNode.size() - 1);
            DfsAddress dfsAddress = DfsAddress.fromString(d.getAddressIp(), d.getAddressPort());
            
            DfsEventMessage responseEventMessage = readEvent.sendEvent(dfsAddress, true);
            
            if(responseEventMessage.getEventParamList().containsKey("STATUS"))
            {
                String status = responseEventMessage.getEventParamList().get("STATUS");

                if(!status.equalsIgnoreCase("OK"))
                {
                    throw new DfsException(status);
                }
            } else {
                throw new InvalidMessageException();
            }
        }
        
        //MERGES FRAGMENTS
        FileSplitter.merge(filePath, fetchFragsPath, 32);
//        reedSolomonDecoder.decode(filePath,fetchFragsPath);
        
        //DELETES FRAGMENTS
        for(DfsFileFragment f : fetchFrags)
        {
            String currentFile = storagePath + f.getGuid();
            File delFile = new File(currentFile);
            delFile.delete();
        }
        
        //SENDS FILE
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
        
        //DELETES FILE
        readFile.delete();
        
        return receivedEventMessage;
    }
}
