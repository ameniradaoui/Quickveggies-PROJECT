package com.quickveggies.impl;

import java.util.List;

import com.quickveggies.entities.Expenditure;

public interface IExpenditureDao {

	void deleteExpenditureType(String name);

	void addExpenditureType(String name);

	List<String> getExpenditureTypeList();

	boolean addExpenditure(Expenditure xpr);

	List<Expenditure> getExpenditureList();

	Expenditure getExpenditureById(int id);

	void deleteExpenditureEntry(int id, boolean writeAuditLog);

}