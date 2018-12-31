package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import com.quickveggies.entities.DSupplierTableLine;
import com.quickveggies.entities.Supplier;

public interface ISupplierDao {

	List<DSupplierTableLine> getSupplierDealEntries(String title) throws SQLException;

	DSupplierTableLine getSupplierDealEntry(int id) throws SQLException;

	int getRowsNum(String tablename);

	void saveSupplier(Supplier supplier) throws SQLException;

	Supplier getSupplierById(int id) throws SQLException, NoSuchElementException;

}