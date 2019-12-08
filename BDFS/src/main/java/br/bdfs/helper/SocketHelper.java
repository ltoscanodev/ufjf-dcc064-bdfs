package br.bdfs.helper;

import br.bdfs.config.DfsConfig;
import br.bdfs.protocol.DfsAddress;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author ltosc
 */
public class SocketHelper 
{
    private static SSLContext getSSLContext() 
            throws KeyStoreException, IOException, NoSuchAlgorithmException, 
            CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(SocketHelper.class.getResourceAsStream("/META-INF/bdfs.jks"), DfsConfig.SSL_KEY_PASSWORD.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, DfsConfig.SSL_KEY_PASSWORD.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
        
        return sslContext;
    }
    
    public static ServerSocket createSSLServerSocket(DfsAddress address) 
            throws KeyStoreException, IOException, NoSuchAlgorithmException, 
            CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        return getSSLContext().getServerSocketFactory().createServerSocket(address.getPort());
    }
    
    public static Socket createSSLSocket(DfsAddress address) 
            throws KeyStoreException, IOException, NoSuchAlgorithmException, 
            CertificateException, UnrecoverableKeyException, KeyManagementException
    {
        return getSSLContext().getSocketFactory().createSocket(address.getIp(), address.getPort());
    }
    
    public static ServerSocket createServerSocket(DfsAddress address) throws IOException
    {
        return new ServerSocket(address.getPort());
    }
    
    public static Socket createSocket(DfsAddress address) throws IOException
    {
        return new Socket(address.getIp(), address.getPort());
    }
}