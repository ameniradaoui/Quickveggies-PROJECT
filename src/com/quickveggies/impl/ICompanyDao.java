package com.quickveggies.impl;

import com.quickveggies.entities.Company;

public interface ICompanyDao {

	Long addCompany(Company company);

	void updateCompany(Company company);

	Company getCompany();

}