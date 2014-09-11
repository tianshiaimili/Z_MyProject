package com.hua.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.R;

public class Httpsdemo extends Activity {
	
	private static final String tag = Httpsdemo.class.getSimpleName();

	private TextView textView;
	private String content = "456";
	private ImageView imageView;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		View button = this.findViewById(R.id.button);
		imageView = (ImageView) findViewById(R.id.imageview);
		textView = (TextView) findViewById(R.id.textview);
		
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				onclick();
				textView.setText(content);
				imageView.setImageBitmap(bitmap);
			}});
	}
	
	
	private void onclick(){
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				try{
//				String str = "https://www.oschina.net/home/login?goto_page=http%3A%2F%2Fwww.oschina.net%2F";
					String str = "https://github.com/tianshiaimili/MyResource/blob/master/res/drawable-hdpi/jkl.jpg";
				
				URL url = new URL(str);
				
				SSLContext sslctxt = SSLContext.getInstance("TLS");
				
				sslctxt.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
				
				HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
				
				conn.setSSLSocketFactory(sslctxt.getSocketFactory());
				conn.setHostnameVerifier(new MyHostnameVerifier());
				
				conn.connect();
				
				int respCode = conn.getResponseCode();
				Log.d(tag, "ResponseCode="+respCode);
				InputStream input = conn.getInputStream();
				
				bitmap = BitmapFactory.decodeStream(input);
//				BitmapFactory.
				
				String result = toString(input);
				content = result;
				Log.d(tag, "result:"+result);
				
				input.close();
				
				conn.disconnect();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			private String toString(InputStream input){
				
				String content = null;
				try{
				InputStreamReader ir = new InputStreamReader(input);
				BufferedReader br = new BufferedReader(ir);
				
				StringBuilder sbuff = new StringBuilder();
				while(null != br){
					String temp = br.readLine();
					if(null == temp)break;
					sbuff.append(temp).append(System.getProperty("line.separator"));
				}
				
				content = sbuff.toString();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				return content;
			}	
		}).start();;
	}
	
	
	
	
	
	static class MyX509TrustManager implements X509TrustManager{

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if(null != chain){
				for(int k=0; k < chain.length; k++){
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			Log.d(tag, "check client trusted. authType="+authType);
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if(null != chain){
				for(int k=0; k < chain.length; k++){
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			Log.d(tag, "check servlet trusted. authType="+authType);
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			
			Log.d(tag, "get acceptedissuer");
			
			return null;
		}
		
		
		private void print(X509Certificate cer){
			
			int version = cer.getVersion();
			String sinname = cer.getSigAlgName();
			String type = cer.getType();
			String algorname = cer.getPublicKey().getAlgorithm();
			BigInteger serialnum = cer.getSerialNumber();
			Principal principal = cer.getIssuerDN();
			String principalname = principal.getName();
			
			Log.d(tag, "version="+version+", sinname="+sinname+", type="+type+", algorname="+algorname+", serialnum="+serialnum+", principalname="+principalname);
		}
		
	}
	
	static class MyHostnameVerifier implements HostnameVerifier{

		@Override
		public boolean verify(String hostname, SSLSession session) {
			Log.d(tag, "hostname="+hostname+",PeerHost= "+session.getPeerHost());
			return true;
		}
		
	}

}
