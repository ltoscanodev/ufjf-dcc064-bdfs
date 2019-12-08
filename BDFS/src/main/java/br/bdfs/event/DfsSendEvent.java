package br.bdfs.event;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.exceptions.NotFoundException;
import br.bdfs.exceptions.ResponseTimeoutException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.helper.SocketHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author ltosc
 */
public abstract class DfsSendEvent extends DfsEvent
{
    private enum DataMessageType { SendFile, ReceiveFile };
    
    public DfsSendEvent(DfsProtocol protocol) 
    {
        super(protocol);
    }
    
    public abstract DfsEventMessage sendEvent(DfsAddress remoteAddress, boolean waitForResponse) throws DfsException, IOException;
    
    protected DfsEventMessage sendMessage(DfsAddress remoteAddress, DfsEventMessage sendEventMessage, boolean waitForResponse)
            throws DfsException, IOException
    {
        Socket clientSocket = null;
        DfsEventMessage responseEventMessage = null;
        
        try
        {
            if (DfsConfig.USE_SSL_SOCKET) 
            {
                clientSocket = SocketHelper.createSSLSocket(remoteAddress);
                ((SSLSocket) clientSocket).startHandshake();
            } 
            else
            {
                clientSocket = SocketHelper.createSocket(remoteAddress);
            }
            
            clientSocket.setSoTimeout(DfsConfig.SOCKET_TIMEOUT);
            
            String sendDataPath = sendEventMessage.getEventParamList().get("SEND_DATA_PATH");
            String receiveDataPath = sendEventMessage.getEventParamList().get("RECEIVE_DATA_PATH");
            
            if(!ObjectHelper.strIsNullOrEmpty(sendDataPath))
            {
                File sendFile = new File(sendDataPath);
                
                if (!sendFile.exists()) 
                {
                    throw new NotFoundException("Arquivo não existe");
                }
                
                sendEventMessage.getEventParamList().remove("SEND_DATA_PATH");
                sendEventMessage.getEventParamList().put("DATA_LENGTH", String.valueOf(sendFile.length()));
                
                responseEventMessage = sendMessage(clientSocket, sendEventMessage, DataMessageType.SendFile, sendFile, waitForResponse);
            }
            else if(!ObjectHelper.strIsNullOrEmpty(receiveDataPath))
            {
                File receiveFile = new File(receiveDataPath);
                
                if (receiveFile.exists()) 
                {
                    throw new NotFoundException("Arquivo já existe");
                }
                
                sendEventMessage.getEventParamList().remove("RECEIVE_DATA_PATH");
                responseEventMessage = sendMessage(clientSocket, sendEventMessage, DataMessageType.ReceiveFile, receiveFile, waitForResponse);
            }
            else
            {
                responseEventMessage = sendMessage(clientSocket, sendEventMessage, null, null, waitForResponse);
            }
        } 
        catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException ex) 
        {
            throw new DfsException(String.format("Não foi possível estabelecer uma conexão segura com %s: %s", remoteAddress.toString(), ex.getMessage()));
        }
        catch(IOException ex)
        {
            throw new DfsException(String.format("Ocorreu um erro durante a comunicação com %s: %s", remoteAddress.toString(), ex.getMessage()));
        }
        finally
        {
            if (!ObjectHelper.isNull(clientSocket))
            {
                clientSocket.close();
                LogHelper.logDebug(String.format("Conexão %s encerrada", remoteAddress.toString()));
            }
        }
        
        return responseEventMessage;
    }
    
    private DfsEventMessage sendMessage(Socket clientSocket, DfsEventMessage sendEventMessage, DataMessageType dataMsgFile, File dataFile, boolean waitForResponse) 
            throws DfsException, IOException
    {
        DfsAddress remoteAddress = new DfsAddress(clientSocket.getInetAddress(), clientSocket.getPort());
        DfsEventMessage responseEventMessage = null;
        
        sendEventMessage.getEventParamList().put("WAIT_FOR_RESPONSE", String.valueOf(waitForResponse));

        BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), DfsConfig.MSG_CHARSET));
        bufferWriter.write(sendEventMessage.toString());
        bufferWriter.flush();

        LogHelper.logDebug(String.format("Mensagem enviada para %s: %s", remoteAddress.toString(), sendEventMessage.toString()));
        
        if(!ObjectHelper.isNull(dataMsgFile) && (dataMsgFile.equals(DataMessageType.SendFile)) && !ObjectHelper.isNull(dataFile))
        {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
            
            byte[] readBuffer = new byte[DfsConfig.FILE_RW_BUFFER_LENGTH];
            long dataLength = dataFile.length();
            long totalRead = 0;
            long read;
            
            LogHelper.logDebug(String.format("Enviando dados para %s...", remoteAddress.toString()));
            
            try(FileInputStream fileInputStream = new FileInputStream(dataFile))
            {
                do 
                {
                    read = Math.min((dataLength - totalRead), DfsConfig.FILE_RW_BUFFER_LENGTH);
                    fileInputStream.read(readBuffer, 0, Math.toIntExact(read));
                    totalRead += read;
                    
                    bufferedOutputStream.write(readBuffer, 0, Math.toIntExact(read));
                } 
                while (totalRead < dataLength);
                
                bufferedOutputStream.flush();
            }
            
            LogHelper.logDebug(String.format("Dados enviados para %s", remoteAddress.toString()));
        }

        if (waitForResponse) 
        {
            try 
            {
                LogHelper.logDebug(String.format("Aguardando resposta da mensagem enviada para %s...", remoteAddress.toString()));
                
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), DfsConfig.MSG_CHARSET));
                String response;
                
                if(!ObjectHelper.isNull(dataMsgFile) && (dataMsgFile.equals(DataMessageType.ReceiveFile)) && !ObjectHelper.isNull(dataFile))
                {
                    response = bufferReader.readLine();
                    responseEventMessage = DfsEventMessage.fromString(response);
                    
                    if(responseEventMessage.getEventParamList().containsKey("STATUS"))
                    {
                        String status = responseEventMessage.getEventParamList().get("STATUS");
                        throw new DfsException(status);
                    }
                    else if(!responseEventMessage.getEventParamList().containsKey("DATA_LENGTH"))
                    {
                        throw new InvalidMessageException();
                    }
                    
                    String strDataLength = responseEventMessage.getEventParamList().get("DATA_LENGTH");
                    
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());

                    byte[] readBuffer = new byte[DfsConfig.FILE_RW_BUFFER_LENGTH];
                    long dataLength = Long.valueOf(strDataLength);
                    long totalRead = 0;
                    long read;

                    LogHelper.logDebug(String.format("Recebendo dados de %s...", remoteAddress.toString()));

                    try (FileOutputStream fileOutputStream = new FileOutputStream(dataFile))
                    {
                        do 
                        {
                            read = Math.min((dataLength - totalRead), DfsConfig.FILE_RW_BUFFER_LENGTH);
                            bufferedInputStream.read(readBuffer, 0, Math.toIntExact(read));
                            totalRead += read;

                            fileOutputStream.write(readBuffer, 0, Math.toIntExact(read));
                        } while (totalRead < dataLength);

                        fileOutputStream.flush();
                    }

                    LogHelper.logDebug(String.format("Dados recebidos de %s", remoteAddress.toString()));
                }
                
                response = bufferReader.readLine();
                LogHelper.logDebug(String.format("Resposta recebida da mensagem enviada para %s: %s", remoteAddress.toString(), response));

                responseEventMessage = DfsEventMessage.fromString(response);
            } 
            catch (SocketTimeoutException ex) 
            {
                throw new ResponseTimeoutException();
            }
        }
        
        return responseEventMessage;
    }
}
