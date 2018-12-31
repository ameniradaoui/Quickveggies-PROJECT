package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;

import com.quickveggies.entities.StorageBuyerDeal;

public interface IStorageBuyerDealDao {

	void addStorageBuyerDealInfo(Integer buyerDealLineId, Integer strorageDealLineId);

	List<StorageBuyerDeal> getStorageBuyerDeals();

	StorageBuyerDeal getStorageBuyerDeal(Integer id) throws SQLException;

	int getStorageDealsCount(String storeType);

}