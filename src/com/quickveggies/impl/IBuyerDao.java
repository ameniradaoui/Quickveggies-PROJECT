package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.ExpenseInfo;

public interface IBuyerDao {

	void deleteBuyerExpenseInfo(String name);

	String[] getBuyerDealEntryLineFromSql(int id) throws SQLException, NoSuchElementException;

	DBuyerTableLine getBuyerDealEntry(int id) throws SQLException, NoSuchElementException;

	List<DBuyerTableLine> getBuyerDealEntries(String buyerTitle, String[] skipBuyers)
			throws SQLException, NoSuchElementException;

	List<ExpenseInfo> getBuyerExpenseInfoList();

	void saveBuyer(Buyer buyer) throws SQLException;

	Buyer getBuyerByName(String name) throws SQLException, NoSuchElementException;

	Buyer getBuyerById(int id) throws SQLException, NoSuchElementException;

	List<Buyer> getBuyers() throws SQLException;

	int getRowsNum(String tablename);

	Vector<Vector<Object>> getAllBuyers() throws SQLException;

}