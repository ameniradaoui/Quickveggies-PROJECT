package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import com.quickveggies.entities.ExpenseInfo;

public interface IExpenseDao {

	String[] getExpenseEntryLineFromSql(Long id) throws SQLException, NoSuchElementException;

	String getLastExpenseType(Long id) throws SQLException, NoSuchElementException;

	String getLastExpense(Long id) throws SQLException, NoSuchElementException;

	void updateExpenseInfo(String name, String type, String defaultAmount);

	void updateBuyerExpenseInfo(String name, String type, String defaultAmount);

	void addExpenseInfo(String name, String type, String defaultAmount);

	void addBuyerExpenseInfo(String name, String type, String defaultAmount);

	ExpenseInfo getExpenseInfoFor(String name);

	ExpenseInfo getBuyerExpenseInfoFor(String name);

	List<ExpenseInfo> getExpenseInfoList();

	List<ExpenseInfo> getBuyerExpenseInfoList();

	void deleteExpenseInfo(String name);

	

}