package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.BuyerSelectionFilter;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.ExpenseInfo;

public interface IBuyerDao {

	void deleteBuyerExpenseInfo(String name);

	DBuyerTableLine getBuyerDealEntryLineFromSql(int id) throws SQLException, NoSuchElementException;

	
	List<DBuyerTableLine> getBuyerDealEntries(String buyerTitle, String[] skipBuyers)
			throws SQLException, NoSuchElementException;

	List<ExpenseInfo> getBuyerExpenseInfoList();

	void saveBuyer(Buyer buyer) throws SQLException;

	Buyer getBuyerByName(String name) throws SQLException, NoSuchElementException;

	Buyer getBuyerById(Long id) throws SQLException, NoSuchElementException;

	List<Buyer> getBuyers() throws SQLException;

	Long getRowsNum(String tablename);

	//Vector<Vector<Object>> getAllBuyers() throws SQLException;

	List<DBuyerTableLine> getListByBuyerType(String title);

	

	List<BuyerSelectionFilter> getSelectionFiler();

	

	List<Buyer> getBuyerByDate(BuyerSelectionFilter filter);

	List<Buyer> getListByBuyerTitle(String name);

	DBuyerTableLine getBuyerDealEntry(Long id) throws SQLException, NoSuchElementException;

	

}