package com.quickveggies.entities;

import java.io.Serializable;

public class BuyerSelectionFilter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public BuyerSelectionFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public static BuyerSelectionFilter fromString(String t1) {
	
		BuyerSelectionFilter  ret = new BuyerSelectionFilter();
		ret.setTitle(t1);
		
		return ret;
	}
	
}
