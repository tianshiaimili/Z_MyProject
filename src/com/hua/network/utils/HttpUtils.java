package com.hua.network.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @author zero
 *
 */
public class HttpUtils {

	private static final int TIME_OUT = 8000;
	protected static final int SHOW_RESPONSE = 0;

	/**
	 * 注意 在HttpRequestListener 的onsuccess方法中返回的值是数据响应的值，
	 * 如果想update到UI上 建议使用Handler 来更新
	 * @param address
	 * @param listener
	 * @return
	 */
	public static String sendHttpRequst(final String address,
			final HttpRequestListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				LogUtils2.i("TAG..............");
				HttpURLConnection connection = null;
				try {
					if (address != null) {
						LogUtils2.i("TAG..............");
						URL url = new URL(address);
						connection = (HttpURLConnection) url.openConnection();
						connection.setConnectTimeout(TIME_OUT);
						connection.setReadTimeout(TIME_OUT);
						connection.setDoInput(true);
						connection.setDoOutput(true);
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in));
						//
						StringBuffer response = new StringBuffer();
						//
						String line;
						while ((line = reader.readLine()) != null) {

							response.append(line);

						}

						if (listener != null) {
							listener.onSuccess(response.toString());
						}

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (listener != null) {
						listener.onError(e.getMessage());
					}
				} finally {

					if (connection != null) {
						connection.disconnect();
					}

				}

			}
		}).start();

		return null;
	}

	public interface HttpRequestListener {

		public void onSuccess(String respones);

		public void onError(String failedMsg);

	};

	

//	private void sendRequestWithHttpURLConnection() {
//		// 开启线程来发起网络请求
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				HttpURLConnection connection = null;
//				try {
//					URL url = new URL("http://www.baidu.com");
//					connection = (HttpURLConnection) url.openConnection();
//					connection.setRequestMethod("GET");
//					connection.setConnectTimeout(8000);
//					connection.setReadTimeout(8000);
//					connection.setDoInput(true);
//					connection.setDoOutput(true);
//					InputStream in = connection.getInputStream();
//					// 下面对获取到的输入流进行读取
//					BufferedReader reader = new BufferedReader(
//							new InputStreamReader(in));
//					StringBuilder response = new StringBuilder();
//					String line;
//					while ((line = reader.readLine()) != null) {
//						response.append(line);
//					}
//					Message message = new Message();
//					message.what = SHOW_RESPONSE;
//					// 将服务器返回的结果存放到Message中
//					message.obj = response.toString();
//					handler.sendMessage(message);
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					if (connection != null) {
//						connection.disconnect();
//					}
//				}
//			}
//		}).start();
//	}

	
	public static void sendRequestWithHttpClient(final int num ,final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = null;
				if(num == 0 || num == 1){
					url = "http://flash.weather.com.cn/wmaps/xml/china.xml";
				}else {
					url = "http://www.weather.com.cn/data/cityinfo/101010100.html";
				}
				
				
				try {
					HttpClient httpClient = new DefaultHttpClient();
					// 指定访问的服务器地址是电脑本机
					HttpGet httpGet = new HttpGet(
							url);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						
						switch (num) {
						case 0:
							 HttpUtils.parseXMLWithSAX(response);
//							 response = new ContentHandler().getMap().get(0) +"| "+new ContentHandler().getMap().get(1);
							 LogUtils2.i("response=="+response);
							break;
							
						case 1:
							HttpUtils.parseXMLWithPull(response);
							break;
							
						case 2:
							HttpUtils.parseJSONWithGSON(response);
							break;
							
						case 3:
							HttpUtils.parseJSONWithJSONObject(response);
							break;
							
						default:
							break;
						}
						
						 Message message = new Message();
						 message.what = SHOW_RESPONSE;
						 // 将服务器返回的结果存放到Message中
						 message.obj = response.toString();
						 handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	
	
	
	public static void parseJSONWithGSON(String jsonData) {
		Gson gson = new Gson();
		List<App> appList = gson.fromJson(jsonData, new TypeToken<List<App>>() {
		}.getType());
		for (App app : appList) {
			Log.d("MainActivity", "id is " + app.getId());
			Log.d("MainActivity", "name is " + app.getName());
			Log.d("MainActivity", "version is " + app.getVersion());
		}
	}

	public static void parseJSONWithJSONObject(String jsonData) {
		try {
//			JSONArray jsonArray = new JSONArray(jsonData);
//			for (int i = 0; i < jsonArray.length(); i++) {
//				JSONObject jsonObject = jsonArray.getJSONObject(i);
//				String id = jsonObject.getString("id");
//				String name = jsonObject.getString("name");
//				String version = jsonObject.getString("version");
//				Log.d("MainActivity", "id is " + id);
//				Log.d("MainActivity", "name is " + name);
//				Log.d("MainActivity", "version is " + version);
//			}
			
			JSONObject jsonObject = new JSONObject(jsonData);
			JSONObject jsonObject2 =  jsonObject.getJSONObject("weatherinfo");
			String city = jsonObject2.getString("city");
			LogUtils2.e("city===="+city);
	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parseXMLWithSAX(String xmlData) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			ContentHandler handler = new ContentHandler();
			xmlReader.setContentHandler(handler);
			xmlReader.parse(new InputSource(new StringReader(xmlData)));
//			saxParser.parse(f, dh);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parseXMLWithPull(String xmlData) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlData));
			int eventType = xmlPullParser.getEventType();
			String id = "";
			String name = "";
			String version = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
				// 开始解析某个结点
				case XmlPullParser.START_TAG: {
					if ("id".equals(nodeName)) {
						id = xmlPullParser.nextText();
					} else if ("name".equals(nodeName)) {
						name = xmlPullParser.nextText();
					} else if ("version".equals(nodeName)) {
						version = xmlPullParser.nextText();
					}
					break;
				}
				// 完成解析某个结点
				case XmlPullParser.END_TAG: {
					if ("app".equals(nodeName)) {
						Log.d("MainActivity", "id is " + id);
						Log.d("MainActivity", "name is " + name);
						Log.d("MainActivity", "version is " + version);
					}
					break;
				}
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
