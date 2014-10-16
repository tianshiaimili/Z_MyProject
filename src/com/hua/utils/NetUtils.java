/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.*;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.*;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.*;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;

// Referenced classes of package com.pccw.gzmobile.utils:
//            AllTrustedX509TrustManager, ApacheSSLSocketFactory

public class NetUtils
{
    public static final class ConnectivityReceiver extends BroadcastReceiver
    {


        private ConnectivityReceiver()
        {
        }
    	
        public static void registerReceiver(Context context, Handler handler)
        {
            context.registerReceiver(mReceiver, mIntentFilter);
            Log.w(TAG, (new StringBuilder()).append(context).append(" register ConnectivityReceiver.").toString());
            String key = getContextKey(context);
            if(handler != null)
            {
                handlerMap.put(key, new WeakReference(handler, queue));
                Log.d(TAG, (new StringBuilder("registerReceiver::Handler is not null, key is ")).append(key).toString());
            }
            initConectivity(context);
            Log.w(TAG, (new StringBuilder("INIT -> OVERALL ")).append(connected).append(", WIFI ").append(wifiConnected).append(", Mobile network ").append(mobileNetworkConnected).toString());
            contextConnectivity.put(key, Boolean.valueOf(connected));
        }

        public static void unregisterReceiver(Context context)
        {
            try
            {
                context.unregisterReceiver(mReceiver);
                Log.w(TAG, (new StringBuilder()).append(context).append(" unregister ConnectivityReceiver.").toString());
                contextConnectivity.remove(getContextKey(context));
            }
            catch(Exception e)
            {
                Log.e(TAG, (new StringBuilder()).append(context).append(" unregisterReceiver fail.").toString(), e);
            }
        }

        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            Log.i(TAG, (new StringBuilder()).append(context).append(" receive action: ").append(action).toString());
            if(action.equals("android.net.conn.CONNECTIVITY_CHANGE"))
            {
                String key = getContextKey(context);
                Handler handler = getHandler(key);
                boolean noConnectivity = intent.hasExtra("noConnectivity");
                connected = !noConnectivity;
                Log.v(TAG, (new StringBuilder("Is connected? -> ")).append(connected).toString());
                if(handler != null)
                    handler.obtainMessage(1001, Boolean.valueOf(connected)).sendToTarget();
                Boolean previousConn = (Boolean)contextConnectivity.get(key);
                Log.d(TAG, (new StringBuilder("Previous connectivity is ")).append(previousConn).append(", current is ").append(connected).toString());
                if(handler != null && previousConn != null && previousConn.booleanValue() != connected)
                    handler.obtainMessage(1004, Boolean.valueOf(connected)).sendToTarget();
                contextConnectivity.put(key, Boolean.valueOf(connected));
                NetworkInfo affectedNetwork = (NetworkInfo)intent.getParcelableExtra("networkInfo");
                Log.v(TAG, (new StringBuilder("affected network: ")).append(affectedNetwork).toString());
                Log.v(TAG, (new StringBuilder("other network: ")).append(intent.getParcelableExtra("otherNetwork")).toString());
                if(affectedNetwork != null)
                {
                    if(affectedNetwork.getType() != 1)
                    {
                        mobileNetworkConnected = affectedNetwork.isConnected();
                        if(handler != null)
                            handler.obtainMessage(1003, Boolean.valueOf(mobileNetworkConnected)).sendToTarget();
                    } else
                    {
                        wifiConnected = affectedNetwork.isConnected();
                        if(handler != null)
                            handler.obtainMessage(1002, Boolean.valueOf(wifiConnected)).sendToTarget();
                    }
                } else
                {
                    Log.e(TAG, "NetworkInfo is null retrieve with ConnectivityManager.EXTRA_NETWORK_INFO.");
                }
                handler = null;
            }
            Log.d(TAG, (new StringBuilder("onReceive -> OVERALL ")).append(connected).append(", WIFI ").append(wifiConnected).append(", Mobile network ").append(mobileNetworkConnected).toString());
        }

        private static String getContextKey(Context context)
        {
            String key = (new StringBuilder(String.valueOf(context.getClass().getSimpleName()))).append("@").append(Integer.toHexString(context.hashCode())).toString();
            context = null;
            return key;
        }

        private static Handler getHandler(String key)
        {
            checkQueue();
            WeakReference handlerReferent = null;
            if((handlerReferent = (WeakReference)handlerMap.get(key)) != null)
            {
                Log.d(TAG, (new StringBuilder(String.valueOf(key))).append(" has handler").toString());
                return (Handler)handlerReferent.get();
            } else
            {
                Log.w(TAG, (new StringBuilder(String.valueOf(key))).append(" does not have handler").toString());
                return null;
            }
        }

        private static void checkQueue()
        {
            Reference ref = queue.poll();
            if(ref != null)
            {
                Log.w(TAG, (new StringBuilder("Find reference in queue: ")).append(ref).toString());
                for(Iterator iterator = handlerMap.entrySet().iterator(); iterator.hasNext();)
                {
                    java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                    if(entry.getValue() == ref)
                    {
                        Log.w(TAG, (new StringBuilder("Find reference in map, bound to remove handler in ")).append(entry.getKey()).toString());
                        handlerMap.remove(entry.getKey());
                        break;
                    }
                }

            }
        }

        public static boolean isConnected()
        {
            Log.v(TAG, (new StringBuilder("isConnected ")).append(connected).toString());
            return connected;
        }

        public static boolean isWIFIConnected()
        {
            Log.v(TAG, (new StringBuilder("isWIFIConnected ")).append(wifiConnected).toString());
            return wifiConnected;
        }

        public static boolean isMobileNetworkConnected()
        {
            Log.v(TAG, (new StringBuilder("isMobileNetworkConnected ")).append(mobileNetworkConnected).toString());
            return mobileNetworkConnected;
        }

        private static void initConectivity(Context context)
        {
            NetworkInfo info = NetUtils.getActiveNetwork(context);
            if(info != null && info.isConnected())
            {
                connected = true;
                if(info.getType() == 1)
                    wifiConnected = true;
                else
                    mobileNetworkConnected = true;
            } else
            {
                connected = false;
                wifiConnected = false;
                mobileNetworkConnected = false;
            }
        }

        private static final String TAG = "com/pccw/gzmobile/utils/NetUtils$ConnectivityReceiver.getSimpleName()";
        private static ConnectivityReceiver mReceiver = new ConnectivityReceiver();
        private static IntentFilter mIntentFilter;
        private static boolean connected;
        private static boolean wifiConnected;
        private static boolean mobileNetworkConnected;
        private static ReferenceQueue queue = new ReferenceQueue();
        private static Map handlerMap = new HashMap();
        private static Map contextConnectivity = new HashMap();
        public static final int MESSAGE_OVERALL_CONNECTIVITY = 1001;
        public static final int MESSAGE_WIFI_CONNECTIVITY = 1002;
        public static final int MESSAGE_MOBILE_NETWORK_CONNECTIVITY = 1003;
        public static final int MESSAGE_OVERALL_CONNECTIVITY_CHANGED = 1004;

        static 
        {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            Log.w(TAG, "Instantiate the BroadcastReceiver and its IntentFilter.");
        }

    }

    public static final class HttpMultipart
    {

        private static void writeMixed(OutputStream out, List postbody, String charsetName)
            throws UnsupportedEncodingException, IOException
        {
            for(Iterator iterator = postbody.iterator(); iterator.hasNext(); out.write("\r\n".getBytes(charsetName)))
            {
                HttpMultipart formdata = (HttpMultipart)iterator.next();
                out.write("--TZOTZTOF20120215\r\n".getBytes(charsetName));
                if(formdata.filename != null)
                    out.write((new StringBuilder("Content-Disposition: file; filename=\"")).append(formdata.filename).append("\"").append("\r\n").toString().getBytes(charsetName));
                if(formdata.contentType != null)
                    out.write((new StringBuilder("Content-Type: ")).append(formdata.contentType).append("\r\n").toString().getBytes(charsetName));
                if(formdata.contentTransferEncoding != null)
                {
                    out.write((new StringBuilder("Content-Transfer-Encoding: ")).append(formdata.contentTransferEncoding).toString().getBytes(charsetName));
                    out.write("\r\n".getBytes(charsetName));
                }
                out.write("\r\n".getBytes(charsetName));
                out.write(formdata.data);
            }

            out.write("--TZOTZTOF20120215--\r\n".getBytes(charsetName));
        }

        private static void writePost(OutputStream out, List postbody, String charsetName)
            throws UnsupportedEncodingException, IOException
        {
            if(out == null || postbody == null || postbody.size() == 0)
            {
                System.out.println("out == null || postbody == null || postbody.size() == 0.");
                return;
            }
            for(Iterator iterator = postbody.iterator(); iterator.hasNext();)
            {
                HttpMultipart formdata = (HttpMultipart)iterator.next();
                out.write("-----------------------------305241254917469\r\n".getBytes(charsetName));
                if(formdata.filename != null)
                    out.write((new StringBuilder("Content-Disposition: form-data; name=\"")).append(formdata.name).append("\"; ").append("filename=\"").append(formdata.filename).append("\"").append("\r\n").toString().getBytes(charsetName));
                else
                    out.write((new StringBuilder("Content-Disposition: form-data; name=\"")).append(formdata.name).append("\"").append("\r\n").toString().getBytes(charsetName));
                if(formdata.contentType != null)
                {
                    if(formdata.contentType.contains("multipart/mixed"))
                        formdata.contentType = "multipart/mixed; boundary=TZOTZTOF20120215";
                    out.write((new StringBuilder("Content-Type: ")).append(formdata.contentType).append("\r\n").toString().getBytes(charsetName));
                }
                if(formdata.contentTransferEncoding != null)
                {
                    out.write((new StringBuilder("Content-Transfer-Encoding: ")).append(formdata.contentTransferEncoding).toString().getBytes(charsetName));
                    out.write("\r\n".getBytes(charsetName));
                }
                out.write("\r\n".getBytes(charsetName));
                if(formdata.data != null)
                {
                    out.write(formdata.data);
                    out.write("\r\n".getBytes(charsetName));
                } else
                if(formdata.mixed != null && formdata.mixed.size() > 0)
                    writeMixed(out, formdata.mixed, charsetName);
            }

            out.write("-----------------------------305241254917469--\r\n".getBytes(charsetName));
            out.write("\r\n".getBytes(charsetName));
        }

        private static final String LINE_SEPERATOR = "\r\n";
        private static final String BOUNDARY = "---------------------------305241254917469";
        private static final String START_BOUNDARY = "-----------------------------305241254917469\r\n";
        private static final String END_BOUNDARY = "-----------------------------305241254917469--\r\n";
        private static final String BOUNDARY_MIXED = "TZOTZTOF20120215";
        private static final String START_BOUNDARY_MIXED = "--TZOTZTOF20120215\r\n";
        private static final String END_BOUNDARY_MIXED = "--TZOTZTOF20120215--\r\n";
        public static final String HTTP_CONTENT_TYPE = "multipart/form-data; boundary=---------------------------305241254917469";
        private String name;
        private String filename;
        private String contentType;
        private String contentTransferEncoding;
        private byte data[];
        private List mixed;


        public HttpMultipart(String name, String filename, String contentType, String contentTransferEncoding, byte data[])
        {
            this.name = name;
            this.filename = filename;
            this.contentType = contentType;
            this.contentTransferEncoding = contentTransferEncoding;
            this.data = data;
        }

        public HttpMultipart(String name, String filename, String contentType, String contentTransferEncoding, List mixed)
        {
            this.name = name;
            this.filename = filename;
            this.contentType = contentType;
            this.contentTransferEncoding = contentTransferEncoding;
            this.mixed = mixed;
        }
    }


    private NetUtils()
    {
    }

    private static void trustAllHosts()
    {
        TrustManager trustAllCerts[] = {
            new AllTrustedX509TrustManager()
        };
        try
        {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch(Exception e)
        {
            Log.e(TAG, "Fail to trustAllHosts.", e);
        }
    }

    public static void setHeader(HttpURLConnection conn)
    {
        try
        {
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setDoInput(true);
            conn.setDoOutput(false);//�����post��ʽ�Ļ� ��ô�����Ǳ���Ϊtrue��
            conn.setConnectTimeout(0);
            conn.setReadTimeout(0);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Fail to setHeader.", e);
        }
    }

    public static HttpURLConnection getHttpURLConnection(String urlStr)
        throws IOException
    {
        URL url = null;
        try
        {
            url = new URL(urlStr);
        }
        catch(MalformedURLException e)
        {
            Log.e(TAG, (new StringBuilder("MalformedURLException : Can not create an URL from an incorrect specification of url ")).append(urlStr).toString());
            throw new IOException("NetUtils.getHttpURLConnection() failed with an incorrect specification url.");
        }
        HttpURLConnection http = null;
        if(url.getProtocol().toLowerCase().equals("https"))
        {
            trustAllHosts();
            HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            http = https;
        } else
        {
            http = (HttpURLConnection)url.openConnection();
        }
        setHeader(http);
        return http;
    }

    public static HttpURLConnection getHttpURLConnection(String urlStr, Map requestProperties)
        throws IOException
    {
        HttpURLConnection conn = getHttpURLConnection(urlStr);
        if(requestProperties != null && requestProperties.size() > 0)
        {
            Iterator iterator = requestProperties.entrySet().iterator();
            java.util.Map.Entry entry = null;
            for(; iterator.hasNext(); conn.setRequestProperty((String)entry.getKey(), (String)entry.getValue()))
                entry = (java.util.Map.Entry)iterator.next();

        }
        return conn;
    }

    public static InputStream getInputStream(String urlStr)
        throws IOException
    {
        HttpURLConnection conn = getHttpURLConnection(urlStr);
        conn.connect();
        return checkInputStream(conn);
    }

    public static InputStream getInputStream(String urlStr, Map requestProperties)
        throws IOException
    {
        HttpURLConnection conn = getHttpURLConnection(urlStr, requestProperties);
        conn.connect();
        return checkInputStream(conn);
    }

    public static InputStream getInputStream(String urlStr, Map requestProperties, int connectTimeout, int readTimeout, int attemptTimes, int interval)
    {
        int retry = 0;
        int maxTime = attemptTimes - 1;
        if(maxTime < 0)
            maxTime = 0;
        InputStream is = null;
        HttpURLConnection conn = null;
        while(retry <= maxTime) 
        {
            try
            {
                conn = getHttpURLConnection(urlStr, requestProperties);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
                conn.connect();
                is = checkInputStream(conn);
            }
            catch(Exception e)
            {
                Log.w(TAG, (new StringBuilder("getInputStream() failed with url : ")).append(urlStr).toString(), e);
                is = null;
            }
            if(is != null)
                break;
            Log.w(TAG, (new StringBuilder("Attempt times(starts from 0) = ")).append(retry).toString());
            retry++;
            try
            {
                Thread.sleep(interval);
            }
            catch(InterruptedException e)
            {
                Log.w(TAG, (new StringBuilder("Thread.sleep() occurs exception ")).append(e).toString());
            }
        }
        return is;
    }

    public static byte[] getByteArray(InputStream is)
        throws IOException
    {
        if(is == null)
            return null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte buffer[] = new byte[2048];
        for(int len = 0; (len = is.read(buffer)) != -1;)
            outStream.write(buffer, 0, len);

        outStream.close();
        is.close();
        return outStream.toByteArray();
    }

    public static byte[] getByteArray(String urlStr)
        throws IOException
    {
        return getByteArray(getInputStream(urlStr));
    }

    public static byte[] getByteArray(String urlStr, Map requestProperties, int connectTimeout, int readTimeout, int attemptTimes, int interval)
    {
        int retry = 0;
        int maxTime = attemptTimes - 1;
        if(maxTime < 0)
            maxTime = 0;
        byte bytes[] = null;
        HttpURLConnection conn = null;
        while(retry <= maxTime) 
        {
            try
            {
                conn = getHttpURLConnection(urlStr, requestProperties);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
                conn.connect();
                bytes = getByteArray(checkInputStream(conn));
            }
            catch(Exception e)
            {
                Log.w(TAG, (new StringBuilder("getByteArray() failed with url : ")).append(urlStr).toString(), e);
                bytes = null;
            }
            if(bytes != null)
                break;
            Log.w(TAG, (new StringBuilder("Attempt times(starts from 0) = ")).append(retry).toString());
            retry++;
            try
            {
                Thread.sleep(interval);
            }
            catch(InterruptedException e)
            {
                Log.w(TAG, (new StringBuilder("Thread.sleep() occurs exception ")).append(e).toString());
            }
        }
        return bytes;
    }

    public static InputStream getRangeInputStream(String urlStr, Map requestProperties, int startPos, int endPos)
        throws IOException
    {
        if(requestProperties != null)
        {
            requestProperties.put("Range", (new StringBuilder("bytes=")).append(startPos).append("-").append(endPos != 0 ? ((Object) (Integer.valueOf(endPos))) : "").toString());
            return getInputStream(urlStr, requestProperties);
        } else
        {
            HttpURLConnection conn = getHttpURLConnection(urlStr);
            conn.setRequestProperty("Range", (new StringBuilder("bytes=")).append(startPos).append("-").append(endPos != 0 ? ((Object) (Integer.valueOf(endPos))) : "").toString());
            conn.connect();
            return checkInputStream(conn);
        }
    }

    public static InputStream getGZIPInputStream(HttpURLConnection conn)
        throws IOException
    {
        conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.connect();
        return new GZIPInputStream(checkInputStream(conn));
    }

    public static InputStream getGZIPInputStream(String urlStr)
        throws IOException
    {
        HttpURLConnection conn = getHttpURLConnection(urlStr);
        conn.addRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.connect();
        return new GZIPInputStream(checkInputStream(conn));
    }

    public static boolean checkInputStreamEOF(InputStream is)
        throws IOException
    {
        boolean retval = false;
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        if(br.readLine() == null)
        {
            Log.e(TAG, "InputStream has already reached the end.");
            retval = true;
        } else
        {
            Log.d(TAG, "InputStream is readable.");
            retval = false;
        }
        is.close();
        reader.close();
        br.close();
        return retval;
    }

    public static String readInputStream(InputStream is, String encoding)
        throws IOException
    {
        StringBuilder text = new StringBuilder("");
        InputStreamReader reader = new InputStreamReader(is, encoding);
        BufferedReader br = new BufferedReader(reader);
        for(String str = null; (str = br.readLine()) != null;)
        {
            if(text.length() > 0)
                text.append("\n");
            text.append(str);
        }

        is.close();
        reader.close();
        br.close();
        return text.toString();
    }

    public static InputStream printInputStream(InputStream is, String encoding)
        throws IOException
    {
        byte bytes[] = getByteArray(is);
        System.out.println(new String(bytes, encoding));
        return new ByteArrayInputStream(bytes);
    }

    public static InputStream postMultipartForm(String urlStr, Map requestProperties, List postbody, String charsetName)
        throws IOException
    {
        HttpURLConnection conn = getHttpURLConnection(urlStr);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        boolean multipart = false;
        if(requestProperties != null && requestProperties.size() > 0)
        {
            Iterator iterator = requestProperties.entrySet().iterator();
            java.util.Map.Entry entry = null;
            for(; iterator.hasNext(); conn.setRequestProperty((String)entry.getKey(), (String)entry.getValue()))
                entry = (java.util.Map.Entry)iterator.next();

        }
        if(postbody != null && postbody.size() > 0)
            multipart = true;
        if(multipart)
        {
            Log.d(TAG, "getPostInputStream :: POST parameters not null.");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------305241254917469");
        }
        conn.connect();
        if(multipart)
        {
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            HttpMultipart.writePost(out, postbody, charsetName);
            out.flush();
            out.close();
        }
        return checkInputStream(conn);
    }

    public static String getParams(Map params, boolean getMethod)
    {
        if(params == null)
            return null;
        StringBuilder paramsStr = null;
        int size = 0;
        if(params != null && (size = params.size()) > 0)
        {
            paramsStr = new StringBuilder();
            List parameters = new ArrayList();
            Iterator iterator = params.entrySet().iterator();
            java.util.Map.Entry entry = null;
            for(; iterator.hasNext(); parameters.add((new StringBuilder(String.valueOf((String)entry.getKey()))).append("=").append(URLEncoder.encode((String)entry.getValue())).toString()))
                entry = (java.util.Map.Entry)iterator.next();

            if(getMethod)
                paramsStr.append("?");
            paramsStr.append((String)parameters.get(0));
            for(int i = 1; i < size; i++)
                paramsStr.append("&").append((String)parameters.get(i));

        }
        if(paramsStr != null)
            return paramsStr.toString();
        else
            return null;
    }

    public static InputStream postApplicationForm(String urlStr, Map requestProperties, String postParams, String charsetName)
        throws IOException
    {
        HttpURLConnection conn = getHttpURLConnection(urlStr);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        if(requestProperties != null && requestProperties.size() > 0)
        {
            Iterator iterator = requestProperties.entrySet().iterator();
            java.util.Map.Entry entry = null;
            for(; iterator.hasNext(); conn.setRequestProperty((String)entry.getKey(), (String)entry.getValue()))
                entry = (java.util.Map.Entry)iterator.next();

        }
        conn.connect();
        if(postParams != null)
        {
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(postParams.getBytes(charsetName));
            out.flush();
            out.close();
        }
        return checkInputStream(conn);
    }

    private static String checkResponseCode(int responseCode)
    {
        String message = null;
        switch(responseCode)
        {
        case 100: // 'd'
            message = "Continue";
            break;

        case 101: // 'e'
            message = "Switching Protocols";
            break;

        case 102: // 'f'
            message = "Processing";
            break;

        case 200: 
            message = "OK";
            break;

        case 201: 
            message = "Created";
            break;

        case 202: 
            message = "Accepted";
            break;

        case 203: 
            message = "Non-Authoritative Information";
            break;

        case 204: 
            message = "No Content";
            break;

        case 205: 
            message = "Reset Content";
            break;

        case 206: 
            message = "Partial Content";
            break;

        case 207: 
            message = "Multi-Status";
            break;

        case 226: 
            message = "IM Used";
            break;

        case 300: 
            message = "Multiple Choices";
            break;

        case 301: 
            message = "Moved Permanently";
            break;

        case 302: 
            message = "Found";
            break;

        case 303: 
            message = "See Other";
            break;

        case 304: 
            message = "Not Modified";
            break;

        case 305: 
            message = "Use Proxy";
            break;

        case 306: 
            message = "Switch Proxy";
            break;

        case 307: 
            message = "Temporary Redirect";
            break;

        case 400: 
            message = "Bad Request";
            break;

        case 401: 
            message = "Unauthorized";
            break;

        case 402: 
            message = "Payment Required";
            break;

        case 403: 
            message = "Forbidden";
            break;

        case 404: 
            message = "Not Found";
            break;

        case 405: 
            message = "Method Not Allowed";
            break;

        case 406: 
            message = "Not Acceptable";
            break;

        case 407: 
            message = "Proxy Authentication Required";
            break;

        case 408: 
            message = "Request Timeout";
            break;

        case 409: 
            message = "Conflict";
            break;

        case 410: 
            message = "Gone";
            break;

        case 411: 
            message = "Length Required";
            break;

        case 412: 
            message = "Precondition Failed";
            break;

        case 413: 
            message = "Request Entity Too Large";
            break;

        case 414: 
            message = "Request-URI Too Long";
            break;

        case 415: 
            message = "Unsupported Media Type";
            break;

        case 416: 
            message = "Requested Range Not Satisfiable";
            break;

        case 417: 
            message = "Expectation Failed";
            break;

        case 418: 
            message = "I'm a teapot";
            break;

        case 422: 
            message = "Unprocessable Entity";
            break;

        case 423: 
            message = "Locked";
            break;

        case 424: 
            message = "Failed Dependency";
            break;

        case 425: 
            message = "Unordered Collection";
            break;

        case 426: 
            message = "Upgrade Required";
            break;

        case 449: 
            message = "Retry With";
            break;

        case 500: 
            message = "Internal Server Error";
            break;

        case 501: 
            message = "Not Implemented";
            break;

        case 502: 
            message = "Bad Gateway";
            break;

        case 503: 
            message = "Service Unavailable";
            break;

        case 504: 
            message = "Gateway Timeout";
            break;

        case 505: 
            message = "HTTP Version Not Supported";
            break;

        case 506: 
            message = "Variant Also Negotiates";
            break;

        case 507: 
            message = "Insufficient Storage";
            break;

        case 509: 
            message = "Bandwidth Limit Exceeded";
            break;

        case 510: 
            message = "Not Extended";
            // fall through

        default:
            message = "No valid response code";
            break;
        }
        return (new StringBuilder("HTTP status response code is ")).append(responseCode).append(" ").append(message).toString();
    }

    public static InputStream checkInputStream(HttpURLConnection conn)
        throws IOException
    {
        int invalidResponseCode = -1;
        int responseCode = conn.getResponseCode();
        String codeMsg = checkResponseCode(responseCode);
        InputStream is = conn.getInputStream();
        String inputStreamInstanceName = is.getClass().getName();
        if(inputStreamInstanceName.startsWith("org.apache.harmony.luni.internal.net.www.protocol.http.HttpURLConnectionImpl"))
            inputStreamInstanceName = is.getClass().getSimpleName();
        String requestUrl = conn.getURL().toString();
        String log = (new StringBuilder(String.valueOf(requestUrl))).append("\r\n").append(codeMsg).append(", InputStream is ").append(inputStreamInstanceName).toString();
        if(responseCode >= 200 && responseCode <= 299)
        {
            Log.d(TAG, log);
        } else
        {
            if(responseCode == -1)
            {
                Log.e(TAG, (new StringBuilder(String.valueOf(log))).append(". ResponseCode is -1, abort it.").toString());
                if(is != null)
                    checkInputStreamEOF(is);
                return null;
            }
            Log.w(TAG, log);
        }
        return is;
    }

    public static HttpClient getHttpClient()
    {
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new ApacheSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            org.apache.http.params.HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            org.apache.http.conn.ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        }
        catch(Exception e)
        {
            Log.w(TAG, "Fail to create DefaultHttpClient with parameters.");
        }
        return new DefaultHttpClient();
    }

    public static InputStream httpGet(String urlStr)
        throws ClientProtocolException, IOException
    {
        HttpGet request = new HttpGet(urlStr);
        HttpResponse response = getHttpClient().execute(request);
        Log.d(TAG, (new StringBuilder("HttpGet response status line is ")).append(response.getStatusLine()).toString());
        return response.getEntity().getContent();
    }

    public static InputStream httpPostApplicationForm(String urlStr, Map params, String charsetName)
        throws IllegalStateException, IOException
    {
        HttpPost request = new HttpPost(urlStr);
        List names = new ArrayList();
        java.util.Map.Entry entry;
        for(Iterator iterator = params.entrySet().iterator(); iterator.hasNext(); names.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue())))
            entry = (java.util.Map.Entry)iterator.next();

        request.setEntity(new UrlEncodedFormEntity(names, charsetName));
        HttpResponse response = getHttpClient().execute(request);
        Log.d(TAG, (new StringBuilder("HttpGet response status line is ")).append(response.getStatusLine()).toString());
        return response.getEntity().getContent();
    }

    public static NetworkInfo getActiveNetwork(Context context)
    {
        NetworkInfo info = null;
        try
        {
            ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService("connectivity");
            info = connMgr.getActiveNetworkInfo();
        }
        catch(Exception e)
        {
            Log.e(TAG, "checkNetwork fail.", e);
        }
        return info;
    }

    public static boolean checkConnectivity(Context context)
    {
        NetworkInfo info = getActiveNetwork(context);
        if(info != null && info.isConnected())
        {
            Log.w(TAG, (new StringBuilder("Network connected. Network type is ")).append(info.getTypeName()).toString());
            return true;
        } else
        {
            Log.w(TAG, "No connected network.");
            return false;
        }
    }

    private static boolean checkNetworkType(Context context, int type)
        throws IllegalStateException
    {
        NetworkInfo info = getActiveNetwork(context);
        if(info != null && info.isConnected())
        {
            Log.d(TAG, (new StringBuilder("Network connected. Network type is ")).append(info.getTypeName()).toString());
            return info.getType() == type;
        } else
        {
            throw new IllegalStateException();
        }
    }

    public static boolean isWIFIConnected(Context context)
    {
        try
        {
            return checkNetworkType(context, 1);
        }
        catch(IllegalStateException e)
        {
            return false;
        }
    }

    public static boolean isMobileNetworkConnected(Context context)
    {
        try
        {
            return !checkNetworkType(context, 1);
        }
        catch(IllegalStateException e)
        {
            return false;
        }
    }

    public static boolean isSSIDWifiExisted(Context context, String ssid)
        throws Exception
    {
        WifiManager wifiManager = (WifiManager)context.getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo != null)
        {
            String wifiSSID = wifiInfo.getSSID();
            if(wifiSSID != null && wifiSSID.equals(ssid))
            {
                Log.v(TAG, (new StringBuilder("SSID ")).append(ssid).append(" WIFI exists.").toString());
                return true;
            }
        }
        Log.v(TAG, (new StringBuilder("No SSID ")).append(ssid).append(" WIFI.").toString());
        return false;
    }

    public static void openWirelessSettings(Context context)
    {
        Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
        intent.setFlags(268435456);
        context.startActivity(intent);
    }

    public static void openWlanSettings(Context context)
    {
        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
        intent.setFlags(268435456);
        context.startActivity(intent);
    }

    public static void openMobileNetworkSettings(Context context)
    {
        Intent intent = new Intent("android.settings.DATA_ROAMING_SETTINGS");
        ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
        intent.setComponent(cName);
        intent.setFlags(268435456);
        context.startActivity(intent);
    }

    public static boolean setWifiEnabled(Context context, boolean enabled)
    {
        WifiManager wifiMgr = (WifiManager)context.getSystemService("wifi");
        return wifiMgr.setWifiEnabled(enabled);
    }

    private static final String TAG = "com/pccw/gzmobile/utils/NetUtils.getSimpleName()";
    private static final int REMOTE_STREAM_BUFFER = 2048;
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session)
        {
            return true;
        }

    }
;

}
