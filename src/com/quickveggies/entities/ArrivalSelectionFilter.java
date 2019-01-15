package com.quickveggies.entities;

import java.io.Serializable;

public class ArrivalSelectionFilter implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fruit;
	private int year;
	public String getFruit() {
		return fruit;
	}
	public void setFruit(String fruit) {
		this.fruit = fruit;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public static ArrivalSelectionFilter fromString(String t1) {
		String[] split = t1.split("FY");
		ArrivalSelectionFilter  ret = new ArrivalSelectionFilter();
		ret.setFruit(split[0].trim());
		ret.setYear(Integer.parseInt(split[1].trim()));
		return ret;
	}
	
	
	
	
}
