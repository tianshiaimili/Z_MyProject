package com.hua.network.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PersonHandler extends DefaultHandler{

	private String noteName;
	private List<Person> persons;
	private Person person;
	
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
//		super.startDocument();
		persons = new ArrayList<Person>();
		
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
//		super.startElement(uri, localName, qName, attributes);
		if("Person".equals(localName)){
			person = new Person();
			String id = attributes.getValue(0);
//			Integer tempID = Integer.valueOf(id);
			person.setId(id);
		}
		
		noteName = localName;
		
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
//		super.characters(ch, start, length);
		
		String data = new String(ch, start,length);
		
		if(noteName != null && person != null){
			
			if("name".equals(noteName)){
				person.setName(data);
			}else if("age".equals(noteName)) {
				person.setAge(data);
			}
			
			
		}
		
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
//		super.endElement(uri, localName, qName);
		if("Person".equals(localName)){
			persons.add(person);
			person = null;
		}
		
		noteName = null;
		
	}
	
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}


	public List<Person> getPersons() {
		return persons;
	}


	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
	
}
