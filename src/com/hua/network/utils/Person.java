package com.hua.network.utils;

public class Person {

	private String id;
	private String name;
	private String age;
	public Person(String name, String age) {
		super();
		this.name = name;
		this.age = age;
	}
	public Person(String id, String name, String age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}
	public Person() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

	
}
