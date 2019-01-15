package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.quickveggies.entities.ArrivalSelectionFilter;
import com.quickveggies.entities.DSalesTableLine;

public interface IDsalesTableDao {

	DSalesTableLine getSalesEntryLineFromSql(int id) throws SQLException, NoSuchElementException;

	List<DSalesTableLine> getSalesEntries() throws SQLException, NoSuchElementException;

	DSalesTableLine getSalesEntryLineByDealId(int dealid) throws SQLException, NoSuchElementException;

	List<DSalesTableLine> getSalesEntryLineBySupplierName(String supplier) throws SQLException, NoSuchElementException;

	List<DSalesTableLine> getFuitByTypeAndYear(ArrivalSelectionFilter filter);
	
	
	List<ArrivalSelectionFilter> getSelectionFiler();
	
	

}