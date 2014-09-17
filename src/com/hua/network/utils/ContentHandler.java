package com.hua.network.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import android.util.SparseArray;

public class ContentHandler extends DefaultHandler {

	private String nodeName;

	private StringBuilder id;

	private StringBuilder name;

	private StringBuilder version;
	
	private String city = null;
	private String quName = null;
	private String cityname = null;
	private SparseArray<String> map;
	
	public SparseArray<String> getMap() {
		return map;
	}

	public void setMap(SparseArray<String> map) {
		this.map = map;
	}

	private int count;

//	可参考http://blog.csdn.net/like7xiaoben/article/details/7086159
	
//	<china dn="day">
//	<city quName="黑龙江" pyName="heilongjiang" cityname="哈尔滨" state1="1" state2="4" stateDetailed="多云转雷阵雨" tem1="27" tem2="17" windState="南风小于3级"/>
//	<city quName="吉林" pyName="jilin" cityname="长春" state1="1" state2="1" stateDetailed="多云" tem1="29" tem2="16" windState="西南风小于3级"/>
//	<city quName="辽宁" pyName="liaoning" cityname="沈阳" state1="0" state2="0" stateDetailed="晴" tem1="30" tem2="18" windState="西风转东南风小于3级"/>
//	<city quName="海南" pyName="hainan" cityname="海口" state1="4" state2="3" stateDetailed="雷阵雨转阵雨" tem1="31" tem2="25" windState="微风"/>
//	</china>
	
	@Override
	public void startDocument() throws SAXException {
		id = new StringBuilder();
		name = new StringBuilder();
		version = new StringBuilder();
		map = new SparseArray<String>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		
		if("city".equals(localName)){
			quName = attributes.getValue(0);
			cityname = attributes.getValue(2);
			LogUtils2.d("quName=="+quName+"    cityname="+cityname);
			map.put(count++, quName);
			map.put(count++, cityname);
		}
		
		// 记录当前结点名
		nodeName = localName;
		
		LogUtils2.i("localName=="+localName+"   nodeName="+nodeName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// 根据当前的结点名判断将内容添加到哪一个StringBuilder对象中
//		if ("id".equals(nodeName)) {
//			id.append(ch, start, length);
//		} else if ("name".equals(nodeName)) {
//			name.append(ch, start, length);
//		} else if ("version".equals(nodeName)) {
//			version.append(ch, start, length);
//		}
		
		if("city".equals(nodeName)){
//			quName = 
		}
		
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
//		if ("app".equals(localName)) {
//			Log.d("ContentHandler", "id is " + id.toString().trim());
//			Log.d("ContentHandler", "name is " + name.toString().trim());
//			Log.d("ContentHandler", "version is " + version.toString().trim());
//			id.setLength(0);
//			name.setLength(0);
//			version.setLength(0);
			LogUtils2.i("localName====="+localName);
			if("city".equals(localName)){
				
			}
			
			
	}

	@Override
	public void endDocument() throws SAXException {
		
		LogUtils2.i("map.size=="+map.size());
		LogUtils2.i("map=="+map.get(0));
		
	}

}
