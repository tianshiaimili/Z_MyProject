/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

// Referenced classes of package com.pccw.gzmobile.utils:
//            AllTrustedX509TrustManager

final class ApacheSSLSocketFactory extends SSLSocketFactory
{

    public ApacheSSLSocketFactory(KeyStore truststore)
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        super(truststore);
        sslContext = SSLContext.getInstance("TLS");
        TrustManager trustAllCerts[] = {
            new AllTrustedX509TrustManager()
        };
        sslContext.init(null, trustAllCerts, null);
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
        throws IOException, UnknownHostException
    {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    public Socket createSocket()
        throws IOException
    {
        return sslContext.getSocketFactory().createSocket();
    }

    private SSLContext sslContext;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 481 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/