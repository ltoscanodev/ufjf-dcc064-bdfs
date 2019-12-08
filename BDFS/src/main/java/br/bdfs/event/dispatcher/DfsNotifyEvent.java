package br.bdfs.event.dispatcher;

import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.protocol.DfsAddress;
import java.net.Socket;

/**
 *
 * @author ltosc
 */
public interface DfsNotifyEvent 
{
    public void notifyEvent(DfsAddress remoteAddress, Socket clientSocket, DfsEventMessage receivedEventMessage);
}
