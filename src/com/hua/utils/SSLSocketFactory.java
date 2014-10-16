/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import java.io.IOException;
import java.net.*;
import java.security.*;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpParams;

// Referenced classes of package org.apache.http.conn.ssl:
//            X509HostnameVerifier

public class SSLSocketFactory
    implements LayeredSocketFactory
{

    public SSLSocketFactory(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, HostNameResolver nameResolver)
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        throw new RuntimeException("Stub!");
    }

    public SSLSocketFactory(KeyStore keystore, String keystorePassword, KeyStore truststore)
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        throw new RuntimeException("Stub!");
    }

    public SSLSocketFactory(KeyStore keystore, String keystorePassword)
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        throw new RuntimeException("Stub!");
    }

    public SSLSocketFactory(KeyStore truststore)
        throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        throw new RuntimeException("Stub!");
    }

    public static SSLSocketFactory getSocketFactory()
    {
        throw new RuntimeException("Stub!");
    }

    public Socket createSocket()
        throws IOException
    {
        throw new RuntimeException("Stub!");
    }

    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress, int localPort, HttpParams params)
        throws IOException
    {
        throw new RuntimeException("Stub!");
    }

    public boolean isSecure(Socket sock)
        throws IllegalArgumentException
    {
        throw new RuntimeException("Stub!");
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
        throws IOException, UnknownHostException
    {
        throw new RuntimeException("Stub!");
    }

    public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier)
    {
        throw new RuntimeException("Stub!");
    }

    public X509HostnameVerifier getHostnameVerifier()
    {
        throw new RuntimeException("Stub!");
    }

    public static final String TLS = "TLS";
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = null;
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = null;
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = null;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\Android-4.3-Tools\adt-bundle-windows-x86_64-20140321\sdk\platforms\android-18\android.jar
	Total time: 34 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/