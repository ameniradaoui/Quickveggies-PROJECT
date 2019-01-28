package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;

import com.quickveggies.entities.StorageBuyerDeal;

public interface IStorageBuyerDealDao {

	

	List<StorageBuyerDeal> getStorageBuyerDeals();

	StorageBuyerDeal getStorageBuyerDeal(Long id) throws SQLException;

	int getStorageDealsCount(String storeType);

	void addStorageBuyerDealInfo(Long newBuyerDealId, Long strorageDealLineId);

}