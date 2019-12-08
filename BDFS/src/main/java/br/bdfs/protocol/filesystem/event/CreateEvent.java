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
import br.bdfs.helper.PathHelper;
import br.bdfs.model.DfsDataNode;
import br.bdfs.model.DfsFile;
import br.bdfs.model.controller.helper.DfsDataNodeHelper;
import br.bdfs.model.controller.helper.DfsDirectoryHelper;
import br.bdfs.model.controller.helper.DfsFileFragmentHelper;
import br.bdfs.model.controller.helper.DfsFileHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author igor6
 */
public class CreateEvent extends DfsReceiveEvent
{
    public static final String EVENT_NAME = "CREATE";
    public static final String STORAGE_PATH = "D:\\DFS\\FSNode";
    public static final int DATA_SHARDS = 10;
    public static final int PARITY_SHARDS = 22;

    private final String storagePath;
    private final Socket clientSocket;
    private final DfsAddress clientAddress;
//    private final ReedSolomonEncoder reedSolomonEncoder;

    public CreateEvent(DfsProtocol protocol, Socket clientSocket) 
    {
        super(protocol);
        
        this.storagePath = STORAGE_PATH + File.separator;
        this.clientSocket = clientSocket;
        this.clientAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
//        reedSolomonEncoder = new ReedSolomonEncoder(DATA_SHARDS, PARITY_SHARDS);
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        LogHelper.logDebug("CreateEvent.receiveEvent()");
        
        if(!receivedEventMessage.getEventParamList().containsKey("DATA_ID")
                || !receivedEventMessage.getEventParamList().containsKey("TOKEN")
                || !receivedEventMessage.getEventParamList().containsKey("DATA_LENGTH")
                || !receivedEventMessage.getEventParamList().containsKey("PATH"))
        {
            throw new InvalidMessageException();
        }

        //CHOOSES MOST UNUSED DATA NODES
        List<DfsDataNode> dfsDataNodes = DfsDataNodeHelper.findAvailableDataNodes();
        if(dfsDataNodes.size() < 4)
        {
            throw new NotFoundException("Não há nós de dados suficientes");
        }
        dfsDataNodes = dfsDataNodes.subList(0, 4);
        
        String dataId = receivedEventMessage.getEventParamList().get("DATA_ID");
        String strDataLength = receivedEventMessage.getEventParamList().get("DATA_LENGTH");
        String strPath = receivedEventMessage.getEventParamList().get("PATH");
        String token = receivedEventMessage.getEventParamList().get("TOKEN");
        
        if(!DfsDirectoryHelper.existsUserDirectory(token, strPath))
        {
            throw new NotFoundException("O diretório  não foi encontrado.");
        }
        if(DfsFileHelper.existsFile(token, String.format("%s/%s", strPath, dataId)))
        {
            throw new NotFoundException("O arquivo já existe.");
        }
        
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
        
        //GENERATES LIST OF FRAGMENTS GUID
        List<String> guid = new ArrayList<>();
        for(int i = 0; i < (PARITY_SHARDS + DATA_SHARDS); i++)
        {
            UUID uuid = UUID.randomUUID();
            guid.add(uuid.toString());
        }
        
        //SPLITS FILE
        FileSplitter.split(dataPath, guid, 32);
//        reedSolomonEncoder.encode(dataPath,guid);
        
//        ReedSolomonDecoder reedSolomonDecoder = new ReedSolomonDecoder(DATA_SHARDS,PARITY_SHARDS);
//        List<String> aux = new ArrayList<>();
//        for(String g : guid)
//        {
//            aux.add(storagePath + g);
//        }
//        reedSolomonDecoder.decode(storagePath + "hist_"  + dataId,aux);
        
        //DISPATCHES FILE FRAGMENT
        int dispatched;
        boolean writeFailed = false;
        for(dispatched = 0; dispatched < (PARITY_SHARDS + DATA_SHARDS) && !writeFailed; dispatched++)
        {
            int index = dispatched % 4;
            String currentId = guid.get(dispatched);
            String currentPath = String.format("%s%s", storagePath, currentId);
            
            DfsDataNode auxDataNode = dfsDataNodes.get(index);
            DfsAddress auxAddress = DfsAddress.fromString(auxDataNode.getAddressIp(), auxDataNode.getAddressPort());
            
            WriteEvent writeEvent = new WriteEvent(getProtocol(), currentPath);
            DfsEventMessage responseEventMessage = writeEvent.sendEvent(auxAddress, true);
            
            if(responseEventMessage.getEventParamList().containsKey("STATUS"))
            {
                String status = responseEventMessage.getEventParamList().get("STATUS");

                if(!status.equalsIgnoreCase("OK"))
                {
                    writeFailed = true;
                }
            } else {
                writeFailed = true;
            }
            
            File delFile = new File(currentPath);
            delFile.delete();
        }
        
        //IF THE WRITE HAS FAILED
        //DELETES SHARDS THA HAVE ALREADY BEEN WRITTEN
        if(writeFailed)
        {
            for(int i = dispatched; i >= 0; i--)
            {
                int index = dispatched % 4;
                String currentId = guid.get(i);
                String currentPath = String.format("%s%s", storagePath, currentId);

                DfsDataNode auxDataNode = dfsDataNodes.get(index);
                DfsAddress auxAddress = DfsAddress.fromString(auxDataNode.getAddressIp(), auxDataNode.getAddressPort());

                DelEvent delEvent = new DelEvent(getProtocol(), currentPath);
                delEvent.sendEvent(auxAddress, false);
            }
        }
        
        //CREATES FILE & FILE FRAGMENT REGISTER
        //AFTER DISPATCHING FOR SECURITY REASONS
        String path = String.format("%s/%s", strPath, dataId);
        DfsFile dfsFile = DfsFileHelper.createUserFile(token, dataId, path, strPath, PathHelper.getFileExtension(path), (int) dataLength);
        
        for(int i = 0; i < (PARITY_SHARDS + DATA_SHARDS); i++)
        {
            int index = i % 4;
            String currentId = guid.get(i);
            DfsDataNode auxDataNode = dfsDataNodes.get(index);
//            int size = reedSolomonEncoder.shardSize(dfsFile.getSize());
            int size = 0;
            DfsFileFragmentHelper.createFileFragment(currentId, i, size, auxDataNode, dfsFile);
        }
        
        
        //DELETES FILES IN CACHE
        File delFile = new File(dataPath);
        delFile.delete();
        
        receivedEventMessage.getEventParamList().clear();
        receivedEventMessage.getEventParamList().put("STATUS", "OK");
        
        return receivedEventMessage;
    }
    
}
