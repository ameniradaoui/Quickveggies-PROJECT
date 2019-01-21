package com.quickveggies.entities;

public class GLCode {

  
    
    private String GLCode;
    private String NameOfLedger;
    private String AccountCode;
	private String AccountType;
    private String Description;
    
    
    
    
    
    
    
    
    public GLCode(String gLCode, String nameOfLedger, String accountCode, String accountType, String description) {
		super();
		GLCode = gLCode;
		NameOfLedger = nameOfLedger;
		AccountCode = accountCode;
		AccountType = accountType;
		Description = description;
	}




	public String getGLCode() {
		return GLCode;
	}




	public void setGLCode(String gLCode) {
		GLCode = gLCode;
	}




	public String getAccountCode() {
		return AccountCode;
	}




	public void setAccountCode(String accountCode) {
		AccountCode = accountCode;
	}

    
    
	public GLCode() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	public String getNameOfLedger() {
		return NameOfLedger;
	}
	public void setNameOfLedger(String nameOfLedger) {
		NameOfLedger = nameOfLedger;
	}
	
	public String getAccountType() {
		return AccountType;
	}
	public void setAccountType(String accountType) {
		AccountType = accountType;
	}

	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
    
    
    

}
