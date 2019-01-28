package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import com.quickveggies.entities.Account;
import com.quickveggies.entities.AccountEntryLine;

import javafx.collections.ObservableList;

public interface IAccountEntryLineDao {

	List<AccountEntryLine> getAccountEntryLines(String accountName)
			throws SQLException, NoSuchElementException;

	

	void saveAccountEntryLine(AccountEntryLine entryline);

	

	
	boolean hasAccountEntry(String date, double withdrawal, double deposit, String desc);

	void saveAccount(Account account) throws SQLException;

	void updateAccount(Account account);

	Account getAccountByName(String accountName) throws SQLException;

	Account getAccountById(Long id) throws SQLException;

	void setAccountEntryStatus(Long id, int newStatus, String auditLog);

	void deleteAccountEntry(Long id);

	AccountEntryLine getAccountEntryLine(Long id) throws SQLException, NoSuchElementException;



	
}