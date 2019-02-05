package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.DSupplierTableLine;
import com.quickveggies.entities.Supplier;

public interface ISupplierDao {

	List<DSupplierTableLine> getSupplierDealEntries(String title) throws SQLException;


	//int getRowsNum(String tablename);

	void saveSupplier(Supplier supplier) throws SQLException;



	List<DSupplierTableLine> getListBySupplierType(String title);

	List<Supplier> getListBySupplierTitle(String name);

	DSupplierTableLine getSupplierDealEntry(Long id) throws SQLException;


	Supplier getSupplierById(Long id) throws SQLException, NoSuchElementException;


	List<String> getEmails();


	List<String> getSMS();


	Map<String, String> getEmailMapper();

}