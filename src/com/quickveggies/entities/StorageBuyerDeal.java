package com.quickveggies.entities;

public class StorageBuyerDeal {
	
	  public StorageBuyerDeal() {
		super();
		// TODO Auto-generated constructor stub
	}
	private Long buyerDealLineId ;
	  private Long strorageDealLineId ;
	  
	  public StorageBuyerDeal(Long buyerDealLineId, Long strorageDealLineId) {
			this.strorageDealLineId = strorageDealLineId;
			this.buyerDealLineId = buyerDealLineId;
	  }
	  
	  
	public Long getStrorageDealLineId() {
		return strorageDealLineId;
	}
	public void setStrorageDealLineId(Long strorageDealLineId) {
		this.strorageDealLineId = strorageDealLineId;
	}
	public Long getBuyerDealLineId() {
		return buyerDealLineId;
	}
	public void setBuyerDealLineId(Long buyerDealLineId) {
		this.buyerDealLineId = buyerDealLineId;
	}


}
