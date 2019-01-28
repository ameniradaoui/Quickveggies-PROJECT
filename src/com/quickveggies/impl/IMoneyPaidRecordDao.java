package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.model.EntityType;

public interface IMoneyPaidRecordDao {

	Long addMoneyPaidRecdInfo(MoneyPaidRecd mpr);

	List<MoneyPaidRecd> getMoneyPaidRecdList(String partyTitle, EntityType pType);

	List<MoneyPaidRecd> getAllMoneyPaidRecdList(EntityType pType);

	List<MoneyPaidRecd> getAdvanceMoneyPaidList(EntityType pType);

	

	boolean deleteMoneyPaidRecd(Long id);

	Long addMoneyPaidRecdInfo(MoneyPaidRecd mpr, String auditLogMsg);

	MoneyPaidRecd getMoneyPaidRecd(Long id);

}