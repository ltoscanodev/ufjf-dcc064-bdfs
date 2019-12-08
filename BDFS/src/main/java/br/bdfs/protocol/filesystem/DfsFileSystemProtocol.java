package br.bdfs.protocol.filesystem;

import br.bdfs.config.DfsConfig;
import br.bdfs.event.message.DfsEventMessage;
import br.bdfs.event.dispatcher.DfsEventService;
import br.bdfs.exceptions.DfsException;
import br.bdfs.protocol.DfsProtocol;
import br.bdfs.protocol.filesystem.event.CdEvent;
import br.bdfs.protocol.filesystem.event.LoginEvent;
import br.bdfs.protocol.filesystem.event.LogoutEvent;
import br.bdfs.protocol.filesystem.event.LsEvent;
import br.bdfs.protocol.filesystem.event.MkDirEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.HashMap;
import br.bdfs.event.DfsReceiveEvent;
import br.bdfs.helper.LogHelper;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.filesystem.event.CpEvent;
import br.bdfs.protocol.filesystem.event.CreateEvent;
import br.bdfs.protocol.filesystem.event.DtndEvent;
import br.bdfs.protocol.filesystem.event.JoinEvent;
import br.bdfs.protocol.filesystem.event.LeaveEvent;
import br.bdfs.protocol.filesystem.event.MvEvent;
import br.bdfs.protocol.filesystem.event.PingEvent;
import br.bdfs.protocol.filesystem.event.SdEvent;
import br.bdfs.protocol.filesystem.event.RmDirEvent;
import br.bdfs.protocol.filesystem.event.RmEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;

/**
 *
 * @author ltosc
 */
public class DfsFileSystemProtocol extends DfsProtocol
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
        EVENT_LIST.put(DtndEvent.EVENT_NAME, DtndEvent.class);
        EVENT_LIST.put(PingEvent.EVENT_NAME, PingEvent.class);
        EVENT_LIST.put(JoinEvent.EVENT_NAME, JoinEvent.class);
        EVENT_LIST.put(LeaveEvent.EVENT_NAME, LeaveEvent.class);
        EVENT_LIST.put(CreateEvent.EVENT_NAME, CreateEvent.class);
        EVENT_LIST.put(MvEvent.EVENT_NAME, MvEvent.class);
    }
    
    private final String cachePath;
    
    public DfsFileSystemProtocol(DfsEventService eventService, String cachePath)
    {
        super(EVENT_LIST, eventService);
        
        this.cachePath = cachePath;
        File cache = new File(cachePath);
        
        if(!cache.exists())
        {
            cache.mkdirs();
            LogHelper.logDebug("Diretório de cache criado");
        }
    }

    @Override
    public void notifyEvent(DfsAddress remoteAddress, Socket clientSocket, DfsEventMessage receivedEventMessage)
    {
        try 
        {
            Class eventClass = EVENT_LIST.get(receivedEventMessage.getEventName());
            DfsReceiveEvent receivedEvent = (DfsReceiveEvent)eventClass.getDeclaredConstructor(DfsProtocol.class,Socket.class).newInstance(this, clientSocket);
            
            receivedEvent.receiveEvent(remoteAddress, clientSocket, receivedEventMessage);
        } 
        catch (NoSuchMethodException | SecurityException | InstantiationException 
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException 
                | DfsException | IOException ex) 
        {
            LogHelper.logError(String.format("Não foi possível retornar a mensagem de status para %s: %s", remoteAddress.toString(), ex.getMessage()));
            try 
            {
                receivedEventMessage.getEventParamList().clear();
                receivedEventMessage.getEventParamList().put("STATUS", ex.getMessage());

                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), DfsConfig.MSG_CHARSET));
                bufferWriter.write(receivedEventMessage.toString());
                bufferWriter.flush();
            } 
            catch (IOException ex1) 
            {
                LogHelper.logError(String.format("Não foi possível retornar a mensagem de status para %s: %s", remoteAddress.toString(), ex1.getMessage()));
            }
        }
    }

    /**
     * @return the cachePath
     */
    public String getCachePath() {
        return cachePath;
    }
}
