package br.bdfs.node.client;

import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.DfsException;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.client.DfsClientProtocol;
import br.bdfs.node.filesystem.IFileSystem;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class BDFSClientNode implements IFileSystem
{
    private final DfsEventService eventService;
    private final DfsClientProtocol clientProtocol;
    
    public BDFSClientNode(DfsAddress remoteAddress)
    {
        this.eventService = new DfsEventService();
        this.clientProtocol = new DfsClientProtocol(eventService, remoteAddress);
    }
    
    public void login(String username, String password) throws DfsException, IOException
    {
        this.clientProtocol.login(username, password);
    }
    
    public void logout() throws DfsException, IOException
    {
        this.clientProtocol.logout();
    }

    @Override
    public String cd(String path) throws DfsException, IOException 
    {
        return this.clientProtocol.cd(path);
    }

    @Override
    public void cp(String srcDataPath, String dstPath) throws DfsException, IOException 
    {
        this.clientProtocol.cp(srcDataPath, dstPath);
    }

    @Override
    public List<String> ls(String path) throws DfsException, IOException 
    {
        return this.clientProtocol.ls(path);
    }

    @Override
    public void mkdir(String path, boolean recursively) throws DfsException, IOException 
    {
        this.clientProtocol.mkdir(path, recursively);
    }

    @Override
    public void mv(String lclPath, String rmtDataPath) throws DfsException, IOException {
        this.clientProtocol.mv(lclPath, rmtDataPath);
    }

    @Override
    public String pwd() throws DfsException, IOException 
    {
        return this.clientProtocol.getCurrentPath();
    }

    @Override
    public void rm(String path) throws DfsException, IOException 
    {
        this.clientProtocol.rm(path);
    }

    @Override
    public void rmdir(String path) throws DfsException, IOException 
    {
        this.clientProtocol.rmdir(path);
    }

    @Override
    public List<String> sd(String... params) throws DfsException, IOException
    {
        return this.clientProtocol.sd(params);
    }
    
    @Override
    public List<String> dtnd(String...params) throws DfsException, IOException
    {
        return this.clientProtocol.dtnd(params);
    }

    @Override
    public void create(String sendFilePath) throws DfsException, IOException {
        this.clientProtocol.create(sendFilePath);
    }
}
