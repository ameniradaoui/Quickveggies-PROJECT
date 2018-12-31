package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.AccountEntryPayment;

public interface IAccountEntryPayementDao {

	AccountEntryPayment getAccountEntryPayment(int id);

	AccountEntryPayment getAccountEntryPayment(String paymentTable, int paymentId);

	List<AccountEntryPayment> getAccountEntryPayments(int accountEntryId);

	Integer addAccountEntryPayment(AccountEntryPayment payment);

	boolean deleteAccountPayment(int id);

	boolean deleteAccountPaymentByEntryId(int accountEntryId);

	boolean deleteAccountEntryPaymentByEntryId(int entryId);

}