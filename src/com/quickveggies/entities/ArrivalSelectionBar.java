package com.quickveggies.entities;

import java.io.Serializable;

public class ArrivalSelectionBar implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sommenet;
	private int year;
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public static ArrivalSelectionBar fromString(String t1) {
		String[] split = t1.split("FY");
		ArrivalSelectionBar  ret = new ArrivalSelectionBar();
		ret.setSommenet(split[0].trim());
		ret.setYear(Integer.parseInt(split[1].trim()));
		return ret;
	}
	public String getSommenet() {
		return sommenet;
	}
	public void setSommenet(String sommenet) {
		this.sommenet = sommenet;
	}
	
	
	
	
	
}
