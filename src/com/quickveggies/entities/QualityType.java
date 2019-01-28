package com.quickveggies.entities;

public class QualityType {

	private Long id;

	private String name;
	
	public QualityType() {}
	
	public QualityType(String name) {
		this.name = name;
	}
	
	public QualityType(Long id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return id+" "+name;
	}

}
