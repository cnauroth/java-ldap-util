/*
 * CustomSSLSocketFactory.java
 */

package cnauroth.ldaputil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This is a custom SSL socket factory that skips certificate validation.  This will allow any SSL
 * certificate to pass, including self-signed certificates.  This is risky to use in a production
 * setting, but may be useful for testing and troubleshooting.
 */
public final class CustomSSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory delegate;

    public CustomSSLSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            delegate = sslContext.getSocketFactory();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
        public Socket createSocket() throws IOException {
        return this.delegate.createSocket();
    }

    @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
        return this.delegate.createSocket(host, port);
    }

    @Override
        public Socket createSocket(InetAddress host, int port, InetAddress localAddress, int localPort) throws IOException {
        return this.delegate.createSocket(host, port, localAddress, localPort);
    }

    @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return this.delegate.createSocket(host, port);
    }

    @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException, UnknownHostException {
        return this.delegate.createSocket(host, port, localAddress, localPort);
    }

    @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return this.delegate.createSocket(socket, host, port, autoClose);
    }

    @Override
        public String[] getDefaultCipherSuites() {
        return this.delegate.getDefaultCipherSuites();
    }

    @Override
        public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    public static SocketFactory getDefault() {
        System.out.println("Using CustomSSLSocketFactory.");
        return new CustomSSLSocketFactory();
    }
}

