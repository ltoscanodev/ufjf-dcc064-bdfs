package br.bdfs;

import br.bdfs.config.DfsConfig;
import br.bdfs.context.AppContext;
import br.bdfs.exceptions.DfsException;
import br.bdfs.exceptions.InvalidFileNameException;
import br.bdfs.exceptions.InvalidPathException;
import br.bdfs.protocol.DfsAddress;
import br.bdfs.protocol.filesystem.event.DelEvent;
import br.bdfs.protocol.filesystem.event.ReadEvent;
import br.bdfs.protocol.filesystem.event.WriteEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class ProtocolTester 
{
    public static void main(String[] args)
    {
        try
        {
            AppContext.initialize();
            DfsConfig.setDebug(true);
            
            String sendFilePath = "D:\\Data\\Developer\\UFJF\\BDFS\\Project\\BDFS\\target\\test.pdf";
            WriteEvent writeEvent = new WriteEvent(null, sendFilePath);
            writeEvent.sendEvent(DfsAddress.fromString("localhost", 6666), true);
            
            String receiveFilePath = "D:\\Data\\Developer\\UFJF\\BDFS\\Project\\BDFS\\target\\received\\test.pdf";
            ReadEvent readEvent = new ReadEvent(null, receiveFilePath);
            readEvent.sendEvent(DfsAddress.fromString("localhost", 6666), true);
            
            String delFileName = "test.pdf";
            DelEvent delEvent = new DelEvent(null, delFileName);
            delEvent.sendEvent(DfsAddress.fromString("localhost", 6666), true);
        } 
        catch (InvalidPathException | InvalidFileNameException | UnknownHostException ex) 
        {
            Logger.getLogger(ProtocolTester.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (DfsException | IOException ex) 
        {
            Logger.getLogger(ProtocolTester.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            AppContext.close();
        }
    }
}
