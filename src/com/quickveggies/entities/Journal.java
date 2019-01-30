package com.quickveggies.entities;

import java.io.InputStream;

public class Journal {
	
	private Long journalNo;
	private String account;
	private String debits;
	private String credits;
	private String description;
	private String name;
	private String journalDate;
	private String memo;
	private InputStream attachement;
	public Journal() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Journal(String account, String debits, String credits, String description, String name) {
		super();
		this.account = account;
		this.debits = debits;
		this.credits = credits;
		this.description = description;
		this.name = name;
	}


	public Journal( String account, String debits, String credits, String description, String name,
			String journalDate, String memo, InputStream attachement) {
		super();
		
		this.account = account;
		this.debits = debits;
		this.credits = credits;
		this.description = description;
		this.name = name;
		this.journalDate = journalDate;
		this.memo = memo;
		this.attachement = attachement;
	}
	public Long getJournalNo() {
		return journalNo;
	}
	public void setJournalNo(Long journalNo) {
		this.journalNo = journalNo;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDebits() {
		return debits;
	}
	public void setDebits(String debits) {
		this.debits = debits;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJournalDate() {
		return journalDate;
	}
	public void setJournalDate(String journalDate) {
		this.journalDate = journalDate;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public InputStream getAttachement() {
		return attachement;
	}
	public void setAttachement(InputStream attachement) {
		this.attachement = attachement;
	}
	
	

}
