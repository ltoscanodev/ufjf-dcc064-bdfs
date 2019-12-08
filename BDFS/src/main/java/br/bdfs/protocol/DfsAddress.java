package br.bdfs.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author ltosc
 */
public class DfsAddress 
{
    private final InetAddress ip;
    private final int port;
    
    public DfsAddress(InetAddress ip, int port)
    {
        this.ip = ip;
        this.port = port;
    }
    
    public static DfsAddress fromString(String ip, int port) throws UnknownHostException
    {
        return new DfsAddress(InetAddress.getByName(ip), port);
    }
    
    @Override
    public String toString()
    {
        return String.format("%s:%s", ip.getHostAddress(), port);
    }

    /**
     * @return the ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
}