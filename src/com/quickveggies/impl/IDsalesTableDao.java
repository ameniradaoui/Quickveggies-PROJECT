package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.quickveggies.entities.ArrivalSelectionBar;
import com.quickveggies.entities.ArrivalSelectionFilter;
import com.quickveggies.entities.DSalesTableLine;

public interface IDsalesTableDao {

	

	List<DSalesTableLine> getSalesEntries() throws SQLException, NoSuchElementException;

	DSalesTableLine getSalesEntryLineByDealId(Long dealid) throws SQLException, NoSuchElementException;

	List<DSalesTableLine> getSalesEntryLineBySupplierName(String supplier) throws SQLException, NoSuchElementException;

	List<DSalesTableLine> getFuitByTypeAndYear(ArrivalSelectionFilter filter);
	
	
	List<ArrivalSelectionFilter> getSelectionFiler();

	List<ArrivalSelectionBar> getSelectionBarchart();

	DSalesTableLine getSalesEntryLineFromSql(Long id) throws SQLException, NoSuchElementException;

	Long getNextTransIdForFreshEntry() throws SQLException;
	
	

}