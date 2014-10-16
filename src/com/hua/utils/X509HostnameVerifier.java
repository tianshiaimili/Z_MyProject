/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import java.io.IOException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public interface X509HostnameVerifier
    extends HostnameVerifier
{

    public abstract boolean verify(String s, SSLSession sslsession);

    public abstract void verify(String s, SSLSocket sslsocket)
        throws IOException;

    public abstract void verify(String s, X509Certificate x509certificate)
        throws SSLException;

    public abstract void verify(String s, String as[], String as1[])
        throws SSLException;
}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\Android-4.3-Tools\adt-bundle-windows-x86_64-20140321\sdk\platforms\android-18\android.jar
	Total time: 197 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/