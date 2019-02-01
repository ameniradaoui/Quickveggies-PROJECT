package com.quickveggies.entities;

import java.io.InputStream;

public class Expenditure {

	private Long id;

	private String payee;

	private String type;

	private String amount;
	
	private String date;
	
	private String comment;
	
	private InputStream receipt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public Expenditure() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Expenditure(String payee, String type, String amount, String date, String comment, InputStream receipt) {
		super();
		this.payee = payee;
		this.type = type;
		this.amount = amount;
		this.date = date;
		this.comment = comment;
		this.receipt = receipt;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public InputStream getReceipt() {
		return receipt;
	}

	public void setReceipt(InputStream receipt) {
		this.receipt = receipt;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	


}
