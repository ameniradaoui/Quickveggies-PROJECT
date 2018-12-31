package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.quickveggies.entities.Account;
import com.quickveggies.entities.AccountEntryLine;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.impl.IAccountEntryLineDao;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
public class AccountEntryLineDao implements IAccountEntryLineDao {
	
	 


	public AccountEntryLineDao() {
		super();
		
	}

	@Autowired
	private DataSource dataSource;

	
	@Autowired
    private AuditDao auditDao ;
	@Autowired
    private UserUtils userDao; 
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IAccountEntryLineDao#getAccountEntryLines(java.lang.String)
	 */
    @Override
	public ObservableList<AccountEntryLine> getAccountEntryLines(String accountName)
	            throws SQLException, NoSuchElementException {
	        ObservableList<AccountEntryLine> result = FXCollections.observableArrayList();
	        PreparedStatement query = dataSource.getConnection().prepareStatement(""
	                + "select * from accountEntries where accountName=?;");
	        query.setString(1, accountName);
	        ResultSet set = query.executeQuery();
	        while (set.next()) {
	            AccountEntryLine line = new AccountEntryLine(set.getString("accountName"), set.getString("transIdCol"),
	                    set.getString("dateCol"), set.getString("chqnoCol"), set.getString("descriptionCol"),
	                    set.getDouble("withdrawalCol"), set.getDouble("depositCol"), set.getDouble("balanceCol"),
	                    set.getInt("status"), set.getString("payee"), set.getString("expense"), set.getString("comment"),
	                    set.getInt("parentId"));
	            line.setId(set.getInt("id"));
	            result.add(line);
	        }
	        if (result.isEmpty()) {
	            throw new NoSuchElementException();
	        } else {
	            return result;
	        }
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#getAccountEntryLine(java.lang.Integer)
		 */
	    @Override
		public AccountEntryLine getAccountEntryLine(Integer id)
	            throws SQLException, NoSuchElementException {
	        PreparedStatement query = dataSource.getConnection().prepareStatement(""
	                + "select * from accountEntries where id=?;");
	        query.setInt(1, id);
	        ResultSet set = query.executeQuery();
	        if (set.next()) {
	            AccountEntryLine line = new AccountEntryLine(set.getString("accountName"), set.getString("transIdCol"),
	                    set.getString("dateCol"), set.getString("chqnoCol"), set.getString("descriptionCol"),
	                    set.getDouble("withdrawalCol"), set.getDouble("depositCol"), set.getDouble("balanceCol"),
	                    set.getInt("status"), set.getString("payee"), set.getString("expense"), set.getString("comment"),
	                    set.getInt("parentId"));
	            line.setId(set.getInt("id"));
	            return line;
	        }
	        else {
	            throw new NoSuchElementException();
	        }
	    }
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#saveAccountEntryLine(com.quickveggies.entities.AccountEntryLine)
		 */
	    @Override
		public void saveAccountEntryLine(AccountEntryLine entryline) {
	        String sql = "INSERT INTO accountEntries (accountName, dateCol, chqnoCol, descriptionCol, withdrawalCol, depositCol, "
	                + "balanceCol, status, payee, expense, comment, transIdCol, parentId) values (?, ?, ?, ?, ?, ?, ?, ? , ? , ? , ?, ?, ?);";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            ps.setString(1, entryline.getAccountName());
	            ps.setString(2, entryline.getDateCol());
	            ps.setString(3, entryline.getChqnoCol());
	            ps.setString(4, entryline.getDescriptionCol());
	            ps.setDouble(5, entryline.getWithdrawalCol());
	            ps.setDouble(6, entryline.getDepositCol());
	            ps.setDouble(7, entryline.getBalanceCol());
	            ps.setInt(8, entryline.getStatus());
	            ps.setString(9, entryline.getPayee());
	            ps.setString(10, entryline.getExpense());
	            ps.setString(11, entryline.getComment());
	            ps.setString(12, entryline.getTransIdCol());
	            
	            if (entryline.getParentId() != null) {
	                ps.setInt(13, entryline.getParentId());
	            }
	            else {
	                ps.setInt(13, java.sql.Types.INTEGER);
	            }
	            ps.executeUpdate();
	            int genId = getGeneratedKey(ps);
	            entryline.setId(genId);
	            //Don't need to log every line
//	            
//	            insertAuditRecord(new AuditLog(0, getCurrentUser(), null,
//	                    "ADDED transaction entry to account: ".concat(entryline.getAccountName()),
//	                    "accountEntries", genId));
	        }
	        catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	    int getGeneratedKey(PreparedStatement ps) throws SQLException {
	        ResultSet genKeyRs = ps.getGeneratedKeys();
	        if (genKeyRs != null && genKeyRs.next()) {
	            return genKeyRs.getInt(1);
	        }
	        System.err.println(String.format("Unable to get the generated id for the object %s, setting it to 0", ps.getMetaData().getTableName(1)));
	        return 0;

	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#setAccountEntryStatus(int, int, java.lang.String)
		 */
	    @Override
		public void setAccountEntryStatus(int id, int newStatus, String auditLog) {
	        System.err.println("id=" + id + " status" + newStatus);
	        String sql = "UPDATE accountEntries SET status=? WHERE id=?;";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            ps.setInt(1, newStatus);
	            ps.setInt(2, id);
	            ps.executeUpdate();
	            if (auditLog == null) {
	                if (newStatus == 0) {
	                    auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "UPDATED transaction entry to account: " + id, "accountEntries", id));
	                }
	            }
	            else if (!"".equals(auditLog)) {
	                auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, auditLog, "accountEntries", id));
	            }
	        }
	        catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#deleteAccountEntry(int)
		 */
	    @Override
		public void deleteAccountEntry(int id) {
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement("DELETE FROM accountEntries WHERE id = ?")) {
	            ps.setInt(1, id);
	            ps.executeUpdate();
//	            insertAuditRecord(new AuditLog(0, getCurrentUser(), null, "DELETED exepense info :".concat(id), null, 0));
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#hasAccountEntry(java.lang.String, double, double, java.lang.String)
		 */
	    @Override
		public boolean hasAccountEntry(String date, double withdrawal, double deposit, String desc) {
	        boolean hasRecord = false;
	        String sql = "Select * from accountEntries where dateCol=? and withdrawalCol=? and depositCol=? and descriptionCol=?";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setString(1, date);
	            ps.setDouble(2, withdrawal);
	            ps.setDouble(3, deposit);
	            ps.setString(4, desc);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                hasRecord = true;
	            }
	        } catch (SQLException x) {
	            x.printStackTrace();
	        }
	        return hasRecord;
	    }

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#saveAccount(com.quickveggies.entities.Account)
		 */
	    @Override
		public void saveAccount(Account account) throws SQLException {
	        String accountNumber = account.getAccountNumber();
	        int accountType = account.getAccountType();
	        double balance = account.getBalance();
	        double initBalance = account.getInitBalance();
	        String accountName = account.getAccountName();
	        String phone = account.getPhone();
	        String description = account.getDescription();
	        String bankName = account.getBankName();
	        int lastupdated = account.getLastupdated();
	        // Statement execStmt = connection.createStatement();
	        // execStmt.execute("SET IDENTITY_INSERT banks ON");
	        PreparedStatement statement = dataSource.getConnection().prepareStatement(
	                "INSERT INTO accounts (acc_name,acc_type,acc_number,bank_name,balance,initBalance,phone,description,lastupdated) VALUES(?,?,?,?,?,?,?,?,?)",
	                Statement.RETURN_GENERATED_KEYS);
	        statement.setString(1, accountName);
	        statement.setInt(2, accountType);
	        statement.setString(3, accountNumber);
	        statement.setString(4, bankName);
	        statement.setDouble(5, balance);
	        statement.setDouble(6, initBalance);
	        statement.setString(7, phone);
	        statement.setString(8, description);
	        statement.setInt(9, lastupdated);
	        statement.executeUpdate();
	        Integer key = getGeneratedKey(statement);
	        auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null,
	                "ADDED bank account ".concat(accountName), "accounts", key));
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#updateAccount(com.quickveggies.entities.Account)
		 */
	    @Override
		public void updateAccount(Account account) {
	        String tableName = "accounts";
	        String sqlCommand = "UPDATE " + tableName + " SET lastupdated=?, acc_type=?, "
	                + "bank_name=?, acc_name=?, acc_number=?, phone=?, description=?, "
	                + "initBalance=?, balance=? WHERE id=?";
	        try {
	            System.out.println(sqlCommand);
	            PreparedStatement statement = dataSource.getConnection().prepareStatement(sqlCommand);
	            statement.setInt(1, account.getLastupdated());
	            statement.setInt(2, account.getAccountType());
	            statement.setString(3, account.getBankName());
	            statement.setString(4, account.getAccountName());
	            statement.setString(5, account.getAccountNumber());
	            statement.setString(6, account.getPhone());
	            statement.setString(7, account.getDescription());
	            statement.setDouble(8, account.getInitBalance());
	            statement.setDouble(9, account.getBalance());
	            statement.setInt(10, account.getId());
	            statement.executeUpdate();
//	            String auditMsg = String.format("UPDATED account (Entry No: %d )", account.getId());
//	            insertAuditRecord(new AuditLog(account.getId(), getCurrentUser(),
//	                    null, auditMsg, tableName, account.getId()));
	            String auditMsg = "Added new entries in bank account " + account.getBankName();
	            auditDao.insertAuditRecord(new AuditLog(account.getId(), userDao.getCurrentUser(),
	                    null, auditMsg, tableName, account.getId()));
	        }
	        catch (SQLException e) {
	            System.out.print("sql exception in updateAccount " + e.getMessage());
	        }
	    }

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#getAccountByName(java.lang.String)
		 */
	    public  Account getAccountByName(String accountName) throws SQLException {
	        PreparedStatement query = dataSource.getConnection().prepareStatement("SELECT * FROM accounts WHERE acc_name=?;");
	        query.setString(1, accountName);
	        ResultSet set = query.executeQuery();
	        Account account = null;
	        while (set.next()) {
	            int id = set.getInt(1);
	            String accountNumber = set.getString("acc_number");
	            int accountType = set.getInt("acc_type");
	            double balance = set.getDouble("balance");
	            double initBalance = set.getDouble("initBalance");
	            String phone = set.getString("phone");
	            String description = set.getString("description");
	            String bankName = set.getString("bank_name");
	            int lastupdated = set.getInt("lastupdated");
	            account = new Account(id, accountNumber, accountType, balance, initBalance, accountName, bankName, phone,
	                    description, lastupdated);
	        }
	        return account;
	    }

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryLineDao#getAccountById(int)
		 */
	    @Override
		public Account getAccountById(int id) throws SQLException {
	        ResultSet set = dataSource.getConnection().prepareStatement("SELECT * FROM accounts WHERE id='" + id + "';").executeQuery();
	        set.next();
	        String accountName = set.getString("acc_name");
	        return getAccountByName(accountName);
	    }

		public DataSource getDataSource() {
			return dataSource;
		}

		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		

	
	   
	    
	    
	  
	    
	   

}
