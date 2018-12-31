package com.quickveggies.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

public interface IDatabaseClient {

	int countSpecificRows(String tablename, String columnName, String value) throws SQLException;

	String getStringEntryFromSQL(String tablename, String searchword, String value, String targetword);

	int getIntEntryFromSQL(String tablename, String[] searchwords, String[] values, String targetword);

	int saveEntryToSql(String tableName, String[] colNames, String[] values);

	boolean checkIfTitleExists(String tableName, String title) throws SQLException;

	void updateTableEntry(String tableName, int lineId, String[] cols, String[] values, boolean skipFirst,
			String auditLogMsg);

	void updateTableEntry(String tableName, int lineId, String[] cols, String[] values, boolean skipFirst);

	void updateTableEntry(String tableName, int lineId, String col, String value, String auditLogMsg);

	int getRowsNum(String tablename);

	void deleteTableEntries(String tablename, String keywordName, String keywordValue, boolean resetIdColumn,
			boolean writeAuditLog);

	void deleteTableEntries(String tablename, String keywordName, String keywordValue, boolean resetIdColumn);

	void resetIdColumn(String tablename, int oldRowsNum) throws SQLException;

	DataSource getDataSource();

	void setDataSource(DataSource dataSource);

	/**
	 * Returns the highest id value used by grower number
	 *
	 * @return the highest id value, or 0 if there are no rows in the table
	 * @throws SQLException
	 */
	int getNextTransIdForFreshEntry() throws SQLException;

}