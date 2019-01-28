package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.Expenditure;

public interface IExpenditureDao {

	void deleteExpenditureType(String name);

	void addExpenditureType(String name);

	List<String> getExpenditureTypeList();

	Long addExpenditure(Expenditure xpr);

	List<Expenditure> getExpenditureList();

	Expenditure getExpenditureById(Long id);

	

	void deleteExpenditureEntry(Long id, boolean writeAuditLog);

}