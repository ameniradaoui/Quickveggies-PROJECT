package com.quickveggies.impl;

import java.io.IOException;
import java.util.List;

import com.quickveggies.entities.Company;
import com.quickveggies.entities.CompanyNameSelection;

public interface ICompanyDao {

	Long addCompany(Company company);

	void updateCompany(Company company);

	Company getCompany();

	List<CompanyNameSelection> getAllCompany();

	Long insertCompany(Company item) throws IOException;

}