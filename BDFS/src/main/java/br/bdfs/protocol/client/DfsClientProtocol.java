package br.bdfs.protocol.client;

import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.AuthException;
import br.bdfs.exceptions.DfsException;
import br.bdfs.helper.ObjectHelper;
import br.bdfs.helper.PathHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.node.filesystem.IFileSystem;
import br.bdfs.protocol.DfsProtocol;
import br.bdfs.protocol.client.event.CdEvent;
import br.bdfs.protocol.client.event.CpEvent;
import br.bdfs.protocol.client.event.CreateEvent;
import br.bdfs.protocol.client.event.DtndEvent;
import br.bdfs.protocol.client.event.SdEvent;
import br.bdfs.protocol.client.event.LoginEvent;
import br.bdfs.protocol.client.event.LogoutEvent;
import br.bdfs.protocol.client.event.LsEvent;
import br.bdfs.protocol.client.event.MkDirEvent;
import br.bdfs.protocol.client.event.MvEvent;
import br.bdfs.protocol.client.event.RmDirEvent;
import br.bdfs.protocol.client.event.RmEvent;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ltosc
 */
public class DfsClientProtocol extends DfsProtocol implements IFileSystem
{
    private static final HashMap<String, Class> EVENT_LIST;
    
    static
    {
        EVENT_LIST = new HashMap<>();
        
        EVENT_LIST.put(LoginEvent.EVENT_NAME, LoginEvent.class);
        EVENT_LIST.put(LogoutEvent.EVENT_NAME, LogoutEvent.class);
        EVENT_LIST.put(CdEvent.EVENT_NAME, CdEvent.class);
        EVENT_LIST.put(CpEvent.EVENT_NAME, CpEvent.class);
        EVENT_LIST.put(LsEvent.EVENT_NAME, LsEvent.class);
        EVENT_LIST.put(MkDirEvent.EVENT_NAME, MkDirEvent.class);
        EVENT_LIST.put(RmEvent.EVENT_NAME, RmEvent.class);
        EVENT_LIST.put(RmDirEvent.EVENT_NAME, RmDirEvent.class);
        EVENT_LIST.put(SdEvent.EVENT_NAME, SdEvent.class);
        EVENT_LIST.put(DtndEvent.EVENT_NAME, SdEvent.class);
        EVENT_LIST.put(CreateEvent.EVENT_NAME, CreateEvent.class);
        EVENT_LIST.put(MvEvent.EVENT_NAME, MvEvent.class);
    }
    
    private final DfsAddress remoteAddress;
    private String token;
    
    private String currentPath;
    private String basePath;
    private String homePath;
    
    public DfsClientProtocol(DfsEventService eventService, DfsAddress remoteAddress) 
    {
        super(EVENT_LIST, eventService);
        
        this.remoteAddress = remoteAddress;
        this.token = null;
        this.homePath = null;
        this.basePath = null;
        this.currentPath = null;
    }
    
    public void login(String username, String password) throws DfsException, IOException
    {
        LoginEvent loginEvent = new LoginEvent(this, username, password);
        DfsEventMessage responseEventMessage = loginEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            throw new AuthException(responseEventMessage.getEventParamList().get("STATUS"));
        }
        
        this.token = responseEventMessage.getEventParamList().get("TOKEN");
        this.homePath = this.cd(null);
    }
    
    public void logout() throws DfsException, IOException
    {
        LogoutEvent logoutEvent = new LogoutEvent(this, token);
        logoutEvent.sendEvent(remoteAddress, false);
        this.token = null;
    }
    
    @Override
    public String cd(String path) throws DfsException, IOException
    {
        if (ObjectHelper.strIsNullOrEmpty(path)) 
        {
            if(ObjectHelper.strIsNullOrEmpty(basePath))
            {
                path = "~";
            }
            else
            {
                this.currentPath = basePath;
                return this.currentPath;
            }
        }
        else if (path.startsWith("./")) 
        {
            path = path.replaceFirst(".", "");
        }
        else if (path.equals("..")) 
        {
            path = PathHelper.previousPath(currentPath);
        }
        else if (!path.contains(currentPath))
        {
            path = PathHelper.concatPath(currentPath, path);
        }
        
        CdEvent cdEvent = new CdEvent(this, token, path);
        DfsEventMessage responseEventMessage = cdEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
        }
        
        this.currentPath = responseEventMessage.getEventParamList().get("PATH");
        this.basePath = PathHelper.basePath(currentPath);
        
        return this.currentPath;
    }

    @Override
    public void cp(String lclPath, String rmtDataPath) throws DfsException, IOException 
    {
        if(!rmtDataPath.contains(currentPath))
        {
            rmtDataPath = PathHelper.concatPath(currentPath, rmtDataPath);
        }
        
        CpEvent cpEvent = new CpEvent(this, token, lclPath, rmtDataPath);
        DfsEventMessage responseEventMessage = cpEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }

    @Override
    public List<String> ls(String path) throws DfsException, IOException 
    {
        LsEvent lsEvent;
        
        if(ObjectHelper.isNull(path))
        {
            lsEvent = new LsEvent(this, token, currentPath);
        }
        else
        {
            if (!path.contains(currentPath)) 
            {
                path = PathHelper.concatPath(currentPath, path);
            }
            
            lsEvent = new LsEvent(this, token, path);
        }
        
        DfsEventMessage responseEventMessage = lsEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
        }
        
        String childs = responseEventMessage.getEventParamList().get("DIR");
        String[] childSplit = childs.split(",");

        return Arrays.asList(childSplit);
    }

    @Override
    public void mkdir(String path, boolean recursively) throws DfsException, IOException 
    {
        if(!path.contains(currentPath))
        {
            path = PathHelper.concatPath(currentPath, path);
        }
        
        MkDirEvent mkDirEvent = new MkDirEvent(this, token, path, recursively);
        DfsEventMessage responseEventMessage = mkDirEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }

    @Override
    public void mv(String srcPath, String dstPath) throws DfsException, IOException
    {
        if(!srcPath.contains(currentPath))
        {
            srcPath = PathHelper.concatPath(currentPath, srcPath);
        }
        if(!dstPath.contains(currentPath))
        {
            dstPath = PathHelper.concatPath(currentPath, dstPath);
        }
        
        MvEvent mvEvent = new MvEvent(this, token, srcPath, dstPath);
        DfsEventMessage responseEventMessage = mvEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }

    @Override
    public String pwd() throws DfsException, IOException 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rm(String path) throws DfsException, IOException 
    {
        if (ObjectHelper.strIsNullOrEmpty(path)) 
        {
            throw new DfsException("Nenhum caminho informado");
        }
        
        RmEvent rmEvent = new RmEvent(this, token, currentPath + "/" + path);
        DfsEventMessage responseEventMessage = rmEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }

    @Override
    public void rmdir(String path) throws DfsException, IOException 
    {
        if (ObjectHelper.strIsNullOrEmpty(path)) 
        {
            throw new DfsException("Nenhum caminho informado");
        }
        else if(path.equalsIgnoreCase(currentPath))
        {
            throw new DfsException("Não é possível remover o diretório atual");
        }
        else if(!path.contains(currentPath))
        {
            path = PathHelper.concatPath(currentPath, path);
        }
        
        RmDirEvent rmDirEvent = new RmDirEvent(this, token, path);
        DfsEventMessage responseEventMessage = rmDirEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    @Override
    public List<String> sd(String... params) throws DfsException, IOException
    {
        SdEvent sdEvent = new SdEvent(this, token, params);
        DfsEventMessage responseEventMessage = sdEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
        else if(responseEventMessage.getEventParamList().containsKey("SHARED_DIR"))
        {
            String sharedDir = responseEventMessage.getEventParamList().get("SHARED_DIR");
            String[] sharedDirSplit = sharedDir.split(",");
            
            return Arrays.asList(sharedDirSplit);
        }
        
        return null;
    }
    
    @Override
    public List<String> dtnd(String... params) throws DfsException, IOException
    {
        DtndEvent dtndEvent = new DtndEvent(this, token, params);
        DfsEventMessage responseEventMessage = dtndEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
            
            return null;
        }
        
        String childs = responseEventMessage.getEventParamList().get("DATA_NODES");
        String[] childSplit = childs.split(",");

        return Arrays.asList(childSplit);
    }
    
    @Override
    public void create(String sendFilePath) throws DfsException, IOException 
    {
        CreateEvent mkDirEvent = new CreateEvent(this, token, sendFilePath, currentPath);
        DfsEventMessage responseEventMessage = mkDirEvent.sendEvent(remoteAddress, true);
        
        if(responseEventMessage.getEventParamList().containsKey("STATUS"))
        {
            String status = responseEventMessage.getEventParamList().get("STATUS");
            
            if(!status.equalsIgnoreCase("OK"))
            {
                throw new DfsException(responseEventMessage.getEventParamList().get("STATUS"));
            }
        }
    }
    
    @Override
    public void notifyEvent(DfsAddress remoteAddress, Socket clientSocket, DfsEventMessage receivedEventMessage)
    {
        
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the homePath
     */
    public String getHomePath() {
        return homePath;
    }

    /**
     * @return the currentPath
     */
    public String getCurrentPath() {
        return currentPath;
    }

}
