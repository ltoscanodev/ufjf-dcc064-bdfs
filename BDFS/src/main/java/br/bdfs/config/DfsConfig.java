package br.bdfs.config;

/**
 *
 * @author ltosc
 */
public class DfsConfig 
{
    private static boolean debug;
    
    public static boolean isDebug() {
        return debug;
    }
    
    public static void setDebug(boolean aDebug) {
        debug = aDebug;
    }
    
    public static final boolean USE_SSL_SOCKET = true;
    public static final String SSL_KEY_PASSWORD = "WkAJDaLUp84QUXdH5zW7Yp9d";
    public static final int SOCKET_TIMEOUT = 15000;
    
    public static final int POOL_MIN_THREAD = 1;
    public static final int POOL_MAX_THREAD = 16;
    public static final int POOL_KEEP_TO_ALIVE_THREAD = 30;
    
    public static final String MSG_CHARSET = "UTF-8";
    public static final String MSG_NAME_AND_PARAM_SEPARATOR = ">";
    public static final String MSG_PARAM_SEPARATOR = ";";
    
    public static final String PATH_SEPARATOR = "/";
    public static final int PATH_MAX_LENGTH = 1024;
    public static final int PATH_MAX_NAME_LENGTH = 64;
    
    public static final int FILE_RW_BUFFER_LENGTH = 8192;
    public static final long FILE_MAX_FRAGMENT_SIZE = 102400;
    
}
