package com.hua.network.utils;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.Context;

public class SAXPersonService {

	private String ASSEST_FILE_NAME ="PersonInfo.xml";
	
	public List<Person> getPersonList(Context context) throws Exception{
		
		InputStream inputStream = context.getAssets().open(ASSEST_FILE_NAME);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		PersonHandler handler = new PersonHandler();
		parser.parse(inputStream, handler);
		
		List<Person> persons = handler.getPersons();
		
		return persons;
	}
	
}
