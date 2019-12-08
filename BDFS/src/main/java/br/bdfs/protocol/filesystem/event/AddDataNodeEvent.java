package br.bdfs.protocol.filesystem.event;

import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.exceptions.DfsException;
import br.bdfs.protocol.DfsProtocol;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class AddDataNodeEvent extends DfsReceiveEvent
{
    public AddDataNodeEvent(DfsProtocol protocol) 
    {
        super(protocol);
    }

    @Override
    protected DfsEventMessage receiveEvent(DfsEventMessage receivedEventMessage) throws DfsException, IOException 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
