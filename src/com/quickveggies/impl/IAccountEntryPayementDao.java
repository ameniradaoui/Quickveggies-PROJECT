package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.AccountEntryPayment;

public interface IAccountEntryPayementDao {

	AccountEntryPayment getAccountEntryPayment(Long id);

	AccountEntryPayment getAccountEntryPayment(String paymentTable, Long paymentId);

	

	Long addAccountEntryPayment(AccountEntryPayment payment);

	boolean deleteAccountPayment(Long id);



	boolean deleteAccountEntryPaymentByEntryId(Long entryId);

	List<AccountEntryPayment> getAccountEntryPayments(Long accountEntryId);

	boolean deleteAccountPaymentByEntryId(Long accountEntryId);

}