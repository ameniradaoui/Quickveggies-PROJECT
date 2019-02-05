package com.quickveggies.entities;

import java.io.Serializable;

public class BuyerSelection implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public BuyerSelection() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	private String Email;
	private String firstname;



	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	
	
}
