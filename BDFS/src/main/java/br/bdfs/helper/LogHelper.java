package br.bdfs.helper;

import br.bdfs.config.DfsConfig;

/**
 *
 * @author ltosc
 */
public class LogHelper
{
    private static void log(String type, String msg)
    {
        if(ObjectHelper.strIsNullOrEmpty(type) || ObjectHelper.strIsNullOrEmpty(msg))
        {
            throw new RuntimeException("Tipo ou mensagem de log é nulo ou vazio");
        }
        
        System.out.println(String.format("[DFS %s] > %s", type.toUpperCase(), msg.replaceAll("\n", "")));
    }
    
    public static void logError(String msg)
    {
        log("ERROR", msg);
    }
    
    public static void logInfo(String msg)
    {
        log("INFO", msg);
    }
    
    public static void logDebug(String msg)
    {
        if(DfsConfig.isDebug())
        {
            log("DEBUG", msg);
        }
    }
}
