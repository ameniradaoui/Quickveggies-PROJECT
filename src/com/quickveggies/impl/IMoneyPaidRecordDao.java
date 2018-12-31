package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.model.EntityType;

public interface IMoneyPaidRecordDao {

	Integer addMoneyPaidRecdInfo(MoneyPaidRecd mpr);

	List<MoneyPaidRecd> getMoneyPaidRecdList(String partyTitle, EntityType pType);

	List<MoneyPaidRecd> getAllMoneyPaidRecdList(EntityType pType);

	List<MoneyPaidRecd> getAdvanceMoneyPaidList(EntityType pType);

	MoneyPaidRecd getMoneyPaidRecd(int id);

	boolean deleteMoneyPaidRecd(int id);

	Integer addMoneyPaidRecdInfo(MoneyPaidRecd mpr, String auditLogMsg);

}