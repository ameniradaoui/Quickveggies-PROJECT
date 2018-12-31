package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.quickveggies.entities.Account;
import com.quickveggies.entities.AccountEntryLine;

import javafx.collections.ObservableList;

public interface IAccountEntryLineDao {

	ObservableList<AccountEntryLine> getAccountEntryLines(String accountName)
			throws SQLException, NoSuchElementException;

	AccountEntryLine getAccountEntryLine(Integer id) throws SQLException, NoSuchElementException;

	void saveAccountEntryLine(AccountEntryLine entryline);

	void setAccountEntryStatus(int id, int newStatus, String auditLog);

	void deleteAccountEntry(int id);

	boolean hasAccountEntry(String date, double withdrawal, double deposit, String desc);

	void saveAccount(Account account) throws SQLException;

	void updateAccount(Account account);

	Account getAccountByName(String accountName) throws SQLException;

	Account getAccountById(int id) throws SQLException;

}