package com.quickveggies.entities;

import java.io.Serializable;

public class ExpenditureTypeName implements Serializable{

	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public static ExpenditureTypeName fromString(String t1) {
		
		ExpenditureTypeName  ret = new ExpenditureTypeName();
		ret.setName(t1);
		
		return ret;
	}
}
