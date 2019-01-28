package com.quickveggies.entities;

public class BoxSize {

	private Long id;

	private String name;
	
	public BoxSize() {}
	
	public BoxSize(String name) {
		this.name = name;
	}
	
	public BoxSize(Long id, String name) {
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
