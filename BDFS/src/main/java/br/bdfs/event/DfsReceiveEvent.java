package br.bdfs.event;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidMessageException;
import br.bdfs.helper.LogHelper;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.DfsProtocol;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public abstract class DfsReceiveEvent extends DfsEvent
{
    public DfsReceiveEvent(DfsProtocol protocol) 
    {
        super(protocol);
    }
    
    protected abstract DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException;
    
    public void receiveEvent(DfsAddress remoteAddress, Socket clientSocket, DfsEventMessage receivedEventMessage) throws DfsException, IOException
    {
        LogHelper.logDebug(String.format("Mensagem recebida de %s: %s", remoteAddress.toString(), receivedEventMessage.toString()));
        
        String strWaitForResponse = receivedEventMessage.getEventParamList().get("WAIT_FOR_RESPONSE");
        
        if(ObjectHelper.strIsNullOrEmpty(strWaitForResponse))
        {
            throw new InvalidMessageException();
        }
        
        boolean waitForResponse = Boolean.valueOf(strWaitForResponse);
        
        if(waitForResponse)
        {
            DfsEventMessage responseEventMessage = receiveEvent(receivedEventMessage);
            
            BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), DfsConfig.MSG_CHARSET));
            bufferWriter.write(responseEventMessage.toString());
            bufferWriter.flush();
            
            LogHelper.logDebug(String.format("Resposta enviada para %s: %s", remoteAddress.toString(), responseEventMessage.toString()));
        }
        else
        {
            receiveEvent(receivedEventMessage);
        }
    }
}
