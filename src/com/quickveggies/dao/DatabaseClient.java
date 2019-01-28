package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.DSupplierTableLine;
import com.quickveggies.impl.IDatabaseClient;

@Component
public class DatabaseClient implements IDatabaseClient {

  
	@Autowired
    private DataSource dataSource;
	@Autowired
    private SupplierDao supplierDao ;
	@Autowired
    private BuyerDao buyerDao;
	@Autowired
    private AuditDao auditDao;
	@Autowired
    private UserUtils userDao;
   
    private final static Map<String, String> TABLE_MAP = new LinkedHashMap<>();

    static {
        TABLE_MAP.put("arrival", "Arrival");
        TABLE_MAP.put("buyerDeals", "Buyer Deal");
        TABLE_MAP.put("ladaanBijakSaleDeals", "Ladaan/Bijak Sale Deal");
        TABLE_MAP.put("storageBuyerDeals", "Storage buyer deal");
        TABLE_MAP.put("supplierDeals", "Supplier Deal");
        TABLE_MAP.put("templates", "Template");
        TABLE_MAP.put("buyers1", "buyer");
        TABLE_MAP.put("accountEntries", "account entry");
        TABLE_MAP.put("accounts", "account");
        TABLE_MAP.put("suppliers1", "supplier");
        TABLE_MAP.put("charges", "charges");
        TABLE_MAP.put("expenditures", "expenditure");
    }


   

    protected DatabaseClient() {
    }

   

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#countSpecificRows(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public int countSpecificRows(String tablename, String columnName, String value) throws SQLException {
        int result = 0;
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection
                .prepareStatement("SELECT * FROM " + tablename + " WHERE " + columnName + "='" + value + "';");
        ResultSet set = statement.executeQuery();
        while (set.next()) {
            result++;
        }
        statement.close();
        connection.close();
        return result;
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#getStringEntryFromSQL(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public String getStringEntryFromSQL(String tablename, String searchword, String value, String targetword) {
        try {
            ResultSet set = getResult("select * from " + tablename + " where " + searchword + "='" + value + "';");
            if (!set.next()) {
                throw new NoSuchElementException();
            }
            
            return set.getString(targetword);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#getIntEntryFromSQL(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String)
	 */
    @Override
	public int getIntEntryFromSQL(String tablename, String[] searchwords, String[] values, String targetword) {
        try {
            String searchCommand = "select * from " + tablename + " where ";
            for (int i = 0; i < searchwords.length; i++) {
                if (i < searchwords.length - 1) {
                    searchCommand += searchwords[i] + "='" + values[i] + "' and ";
                } else {
                    searchCommand += searchwords[i] + "='" + values[i] + "';";
                }
            }
            ResultSet set = getResult(searchCommand);
            if (!set.next()) {
                throw new NoSuchElementException();
            }
            return set.getInt(targetword);
        } catch (SQLException e) {
            e.printStackTrace();
            GeneralMethods.errorMsg("entry not found");
            return 0;
        }
    }

    
    
   
   

   
    
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#saveEntryToSql(java.lang.String, java.lang.String[], java.lang.String[])
	 */
    @Override
	public Long saveEntryToSql(String tableName, String[] colNames, String[] values) {
        Long generatedId = 0l;
        String sqlCommand = "insert into " + tableName + " (" + colNames[0];
        for (int i = 1; i < colNames.length; i++) {
            sqlCommand += "," + colNames[i];
        }
        sqlCommand += ") values('" + values[0] + "'";
        for (int i = 1; i < values.length; i++) {
            sqlCommand += ",'" + values[i] + "'";
        }
        sqlCommand += ")";
        System.out.println(sqlCommand);
        try {
        	 Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlCommand, Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
            generatedId = (long) getGeneratedKey(statement);
            String baseMsg = "ADDED Entry for %s (Entry No: %d )";
            auditDao.auditEntry(baseMsg, tableName, values, generatedId);
            
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("sql exception in saveEntryToSql");
        }
       
        return generatedId;
    }

  
   
   

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#checkIfTitleExists(java.lang.String, java.lang.String)
	 */
    @Override
	public boolean checkIfTitleExists(String tableName, String title) throws SQLException {
        ResultSet set = getResult("select * from " + tableName + " where title='" + title + "';");
        if (set.next()) {
        	set.close();
            return true;
        } else {
        	set.close();
            return false;
        }
    }

 
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#updateTableEntry(java.lang.String, int, java.lang.String[], java.lang.String[], boolean, java.lang.String)
	 */
    @Override
	public void updateTableEntry(String tableName, Long lineId, String[] cols,
            String[] values, boolean skipFirst, String auditLogMsg) {
        String sqlCommand = "UPDATE " + tableName + " SET ";
        int valuesStartIndex = 0;
        if (skipFirst) {
            valuesStartIndex = 1;
        }
        sqlCommand += cols[0] + "='" + values[valuesStartIndex] + "'";
        /* skip 1st value which is the sql autogenerated id, that mustn't be
	* altered by user */
        for (int i = valuesStartIndex + 1; i < cols.length; i++) {
            sqlCommand += "," + cols[i] + "=?";
        }
        sqlCommand += " where id='" + lineId + "'";
        String oldValues = null;
        try {
            if (tableName.equals("buyerDeals")) {
                DBuyerTableLine line = buyerDao.getBuyerDealEntry(lineId);
                oldValues = line == null ? null : line.serialize();
            }
            else if (tableName.equals("supplierDeals")) {
                DSupplierTableLine line = supplierDao.getSupplierDealEntry(lineId);
                oldValues = line == null ? null : line.serialize();
            }
        }
        catch (SQLException | NoSuchElementException ex) {
            Logger.getLogger(DatabaseClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String oldValuesFinal = oldValues;
        try {
            System.out.println(sqlCommand);
            PreparedStatement statement = dataSource.getConnection().prepareStatement(sqlCommand);
            for (int i = valuesStartIndex + 1; i < cols.length; i++) {
                statement.setString(i - valuesStartIndex, values[i + valuesStartIndex]);
            }
            statement.executeUpdate();
            String auditMsg;
            if (auditLogMsg == null) {
                auditMsg = String.format("UPDATED Entry for %s (Entry No: %d )", TABLE_MAP.get(tableName), lineId);
            }
            else {
                auditMsg = auditLogMsg;
            }
            if (!"".equals(auditMsg)) {
                auditDao.insertAuditRecord(new AuditLog(lineId, userDao.getCurrentUser(), null, auditMsg, tableName, lineId) {{
                    setOldValues(oldValuesFinal);
                }} );
            }
            statement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.print("sql exception in updateEntry " + e.getMessage());
        }
    }
    
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#updateTableEntry(java.lang.String, int, java.lang.String[], java.lang.String[], boolean)
	 */
    @Override
	public void updateTableEntry(String tableName, Long lineId, String[] cols,
            String[] values, boolean skipFirst) {
        updateTableEntry(tableName, lineId, cols, values, skipFirst, null);
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#updateTableEntry(java.lang.String, int, java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public void updateTableEntry(String tableName, Long lineId, String col,
            String value, String auditLogMsg) {
        String sqlCommand = "UPDATE " + tableName + " SET ";
        sqlCommand += col + "='" + value + "'";
        sqlCommand += " WHERE id='" + lineId + "'";

        try {
            PreparedStatement statement = dataSource.getConnection().prepareStatement(sqlCommand);
            statement.executeUpdate();
            String normalizeTabName = TABLE_MAP.get(tableName);
            if (auditLogMsg != null) {
                if (!"".equals(auditLogMsg)) {
                    auditDao.insertAuditRecord(new AuditLog(0l,userDao.getCurrentUser(), null,
                            auditLogMsg, tableName, lineId));
                }
                statement.close();
                return;
            }
            auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
                    "UPDATED Entry for ".concat(normalizeTabName), tableName, lineId));
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.print("sql exception in updateEntry " + e.getMessage());
        }
    }
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#getRowsNum(java.lang.String)
	 */
    @Override
	public int getRowsNum(String tablename) {
        int rowsNum = 0;
        try {
            ResultSet result = getResult("SELECT COUNT(*) FROM " + tablename);
            while (result.next()) {
                rowsNum = result.getInt(1);
            }
        } catch (SQLException e) {
            System.out.print("sql exception in getRowsNum\n");
        }
        return rowsNum;
 }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#deleteTableEntries(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
    @Override
	public void deleteTableEntries(String tablename, String keywordName,
            String keywordValue, boolean resetIdColumn, boolean writeAuditLog) {
        try {
            String deleteCommand = null, findCommand = null;
            if (!keywordName.equals("ALL") || !keywordValue.equals("ALL")) {
                findCommand = "select * from " + tablename + " where " + keywordName + "='" + keywordValue + "';";
                deleteCommand = "delete from " + tablename + " where " + keywordName + "='" + keywordValue + "';";
            } else {
                findCommand = "select * from " + tablename;
                deleteCommand = "delete from " + tablename;
            }
            int oldRowsNum = getRowsNum(tablename);
            int newRowsNum = oldRowsNum;
            ResultSet rowsToDelete = getResult(findCommand);
            // find how many rows are left
            while (rowsToDelete.next()) {
                newRowsNum--;
            }
            dataSource.getConnection().prepareStatement(deleteCommand).executeUpdate();
            if (writeAuditLog) {
                auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
                        "DELETED Entry for ".concat(TABLE_MAP.get(tablename)), null, 0l));
            }
            if (!resetIdColumn) {
                return;
            }
            // reorganize the id column
            resetIdColumn(tablename, oldRowsNum);
            String reseedIdCommand = "DBCC CHECKIDENT ('" + tablename + "', RESEED, " + newRowsNum + ");";
            dataSource.getConnection().prepareStatement(reseedIdCommand).executeUpdate();
        }
        catch (SQLException e) {
            System.out.print("sql exception in deleteTableEntries " + e.getMessage());
        }
    }
    
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#deleteTableEntries(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
    @Override
	public void deleteTableEntries(String tablename, String keywordName,
            String keywordValue, boolean resetIdColumn) {
        deleteTableEntries(tablename, keywordName, keywordValue, resetIdColumn, true);
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#resetIdColumn(java.lang.String, int)
	 */
    @Override
	public void resetIdColumn(String tablename, int oldRowsNum) throws SQLException {
        // prepare sql table with identity for changes
        PreparedStatement statement = dataSource.getConnection()
                .prepareStatement("ALTER TABLE " + tablename + " SWITCH TO temp_" + tablename + ";");
        statement.executeUpdate();

        int newid = 1;
        for (int rowind = 1; rowind <= oldRowsNum; rowind++) {
            // check if there is a line with this id
            ResultSet res = getResult("select * from temp_" + tablename + " where id='" + rowind + "';");
            if (!res.next()) {
                continue;
            }
            statement = dataSource.getConnection().prepareStatement(
                    "update temp_" + tablename + " set id='" + (newid++) + "' where id='" + rowind + "';");
            // System.out.println("update temp_"+tablename+" set id='"+newid+"'
            // where id='"+rowind+"';");
            statement.executeUpdate();
        }

        statement = dataSource.getConnection().prepareStatement("ALTER TABLE temp_" + tablename + " SWITCH TO " + tablename + ";");
        statement.executeUpdate();
        statement.close();
    }

  

	private int getPayementMethod(String paymentMethod) {
		try {
			return Integer.parseInt(paymentMethod);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			return 0;
		}
	}


 
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#getDataSource()
	 */
    @Override
	public DataSource getDataSource() {
		return dataSource;
	}



	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#setDataSource(javax.sql.DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}



	private ResultSet getResult(String query) throws SQLException {
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }

   
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDatabaseClient#getNextTransIdForFreshEntry()
	 */
   

    int getGeneratedKey(PreparedStatement ps) throws SQLException {
        ResultSet genKeyRs = ps.getGeneratedKeys();
        if (genKeyRs != null && genKeyRs.next()) {
            return genKeyRs.getInt(1);
        }
        System.err.println(String.format("Unable to get the generated id for the object %s, setting it to 0", ps.getMetaData().getTableName(1)));
        return 0;

    }
    /*
	 * Inserts the new fruit object only if there is no entry made earlier for
	 * the same fruit
     */
   
    
  
   
}