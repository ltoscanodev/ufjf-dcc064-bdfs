package br.bdfs.event;

import br.bdfs.protocol.DfsProtocol;

/**
 *
 * @author ltosc
 */
public abstract class DfsEvent 
{
    private final DfsProtocol protocol;
    
    public DfsEvent(DfsProtocol protocol)
    {
        this.protocol = protocol;
    }
    
    /**
     * @return the protocol
     */
    public DfsProtocol getProtocol() {
        return protocol;
    }
}