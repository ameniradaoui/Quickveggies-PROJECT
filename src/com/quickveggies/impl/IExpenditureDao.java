package com.quickveggies.impl;

import java.io.IOException;
import java.util.List;

import com.quickveggies.entities.Expenditure;
import com.quickveggies.entities.ExpenditureType;
import com.quickveggies.entities.ExpenditureTypeName;

public interface IExpenditureDao {

	void deleteExpenditureType(String name);

	void addExpenditureType(String name);

	List<String> getExpenditureTypeList();

	Long addExpenditure(Expenditure xpr) throws IOException;

	List<Expenditure> getExpenditureList();

	Expenditure getExpenditureById(Long id);

	

	void deleteExpenditureEntry(Long id, boolean writeAuditLog);

	List<ExpenditureTypeName> getSelectionFiler();

}