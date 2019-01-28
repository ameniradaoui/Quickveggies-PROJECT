package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.Account;
import com.quickveggies.entities.AccountEntryLine;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.DSalesTableLine;
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

	private SimpleJdbcInsert insertAccount;
	private JdbcTemplate template;
	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insertAccount == null) {
			createInsertAccount();

		}
	}

	private void initInsertEntries() {
		if (insert == null) {
			createInsert();
		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("accountEntries").usingGeneratedKeyColumns("id");
	}

	private void createInsertAccount() {
		insertAccount = new SimpleJdbcInsert(dataSource).withTableName("accounts").usingGeneratedKeyColumns("id");
	}

	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();
		createInsertAccount();
	}

	public AccountEntryLineDao() {
		super();

	}

	@Autowired
	private DataSource dataSource;

	@Autowired
	private AuditDao auditDao;
	@Autowired
	private UserUtils userDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IAccountEntryLineDao#getAccountEntryLines(java.lang.
	 * String)
	 */

	private static RowMapper<AccountEntryLine> mapper = new RowMapper<AccountEntryLine>() {

		@Override
		public AccountEntryLine mapRow(ResultSet set, int rowNum) throws SQLException {
			Long id = set.getLong("id");
			String name = set.getString("accountname");
			String date = set.getString("datecol");
			String description = set.getString("descriptioncol");
			Double withcol = set.getDouble("withdrawalcol");
			Double deposit = set.getDouble("depositcol");
			Double balance = set.getDouble("balancecol");
			String chq = set.getString("chqnocol");
			Integer status = set.getInt("status");
			String paye = set.getString("payee");
			String expense = set.getString("expense");
			String comment = set.getString("comment");
			String transidcol = set.getString("transidcol");
			Long parentid = set.getLong("parentid");

			return new AccountEntryLine(id, name, date, description, withcol, deposit, balance, chq, status, paye,
					expense, comment, transidcol, parentid);
		}

	};
	private static RowMapper<Account> mapperAccount = new RowMapper<Account>() {

		@Override
		public Account mapRow(ResultSet set, int rowNum) throws SQLException {

			Long id = set.getLong(1);
			String accountNumber = set.getString("acc_number");
			int accountType = set.getInt("acc_type");
			double balance = set.getDouble("balance");
			double initBalance = set.getDouble("initBalance");
			String phone = set.getString("phone");
			String description = set.getString("description");
			String accountName = set.getString("acc_name");
			String bankName = set.getString("bank_name");
			int lastupdated = set.getInt("lastupdated");
			return new Account(id, accountNumber, accountType, balance, initBalance, accountName, bankName, phone,
					description, lastupdated);
		}

	};

	@Override
	public List<AccountEntryLine> getAccountEntryLines(String accountName) throws SQLException, NoSuchElementException {

		initTemplate();

		List<AccountEntryLine> list = template.query("select ae.* from accountEntries ae left join "
				+ " accounts ac on ae.accountname=ac.acc_name  " + " where ae.accountname=? ", mapper, accountName);

		return list;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IAccountEntryLineDao#getAccountEntryLine(java.lang.
	 * Integer)
	 */
	@Override
	public AccountEntryLine getAccountEntryLine(Long id) throws SQLException, NoSuchElementException {

		initTemplate();
		String sql = "select * from accountEntries where id=?";
		final AccountEntryLine accountEntryLine = new AccountEntryLine();
		template.query(sql, new Object[] { id }, new RowCallbackHandler() {
			public void processRow(ResultSet set) throws SQLException {
				accountEntryLine.setId(set.getLong("id"));
				accountEntryLine.setAccountName(set.getString("accountName"));
				accountEntryLine.setDateCol(set.getString("dateCol"));
				accountEntryLine.setDescriptionCol(set.getString("descriptionCol"));
				accountEntryLine.setWithdrawalCol(set.getDouble("withdrawalCol"));
				accountEntryLine.setDepositCol(set.getDouble("depositCol"));
				accountEntryLine.setBalanceCol(set.getDouble("balanceCol"));
				accountEntryLine.setChqnoCol(set.getString("chqnoCol"));
				accountEntryLine.setStatus(set.getInt("status"));
				accountEntryLine.setPayee(set.getString("payee"));
				accountEntryLine.setExpense(set.getString("expense"));
				accountEntryLine.setComment(set.getString("comment"));
				accountEntryLine.setTransIdCol(set.getString("transIdCol"));
				accountEntryLine.setParentId(set.getLong("parentId"));

			}
		});
		return accountEntryLine;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IAccountEntryLineDao#saveAccountEntryLine(com.
	 * quickveggies.entities.AccountEntryLine)
	 */

	@Override
	public void saveAccountEntryLine(AccountEntryLine item) {
		initInsertEntries();

		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("accountName", item.getAccountName());
		args.put("dateCol", item.getDateCol());
		args.put("chqnoCol", item.getChqnoCol());
		args.put("descriptionCol", item.getDescriptionCol());
		args.put("withdrawalCol", item.getWithdrawalCol());
		args.put("depositCol", item.getDepositCol());
		args.put("balanceCol", item.getBalanceCol());
		args.put("status", item.getStatus());
		args.put("payee", item.getPayee());
		args.put("expense", item.getExpense());
		args.put("comment", item.getComment());
		args.put("transIdCol", item.getTransIdCol());
		args.put("parentId", item.getParentId());

		Long id = insert.executeAndReturnKey(args).longValue();

		/*
		 * String sql =
		 * "INSERT INTO accountEntries (accountName, dateCol, chqnoCol, descriptionCol, withdrawalCol, depositCol, "
		 * +
		 * "balanceCol, status, payee, expense, comment, transIdCol, parentId) values (?, ?, ?, ?, ?, ?, ?, ? , ? , ? , ?, ?, ?);"
		 * ;
		 * 
		 * 
		 * template.update(sql, entryline.getAccountName() ,
		 * entryline.getDateCol() );
		 * 
		 * ps.setString(1, entryline.getAccountName()); ps.setString(2,
		 * entryline.getDateCol()); ps.setString(3, entryline.getChqnoCol());
		 * ps.setString(4, entryline.getDescriptionCol()); ps.setDouble(5,
		 * entryline.getWithdrawalCol()); ps.setDouble(6,
		 * entryline.getDepositCol()); ps.setDouble(7,
		 * entryline.getBalanceCol()); ps.setInt(8, entryline.getStatus());
		 * ps.setString(9, entryline.getPayee()); ps.setString(10,
		 * entryline.getExpense()); ps.setString(11, entryline.getComment());
		 * ps.setString(12, entryline.getTransIdCol());
		 * 
		 * if (entryline.getParentId() != null) { ps.setInt(13,
		 * entryline.getParentId()); } else { ps.setInt(13,
		 * java.sql.Types.INTEGER); } ps.executeUpdate(); int genId =
		 * getGeneratedKey(ps); entryline.setId(genId); ps.close();
		 * connection.close(); // Don't need to log every line // //
		 * insertAuditRecord(new AuditLog(0, getCurrentUser(), null, // "ADDED
		 * transaction entry to account: //
		 * ".concat(entryline.getAccountName()), // "accountEntries", genId)); }
		 * catch (SQLException ex) { ex.printStackTrace(); }
		 */
	}

	int getGeneratedKey(PreparedStatement ps) throws SQLException {
		ResultSet genKeyRs = ps.getGeneratedKeys();
		if (genKeyRs != null && genKeyRs.next()) {
			return genKeyRs.getInt(1);
		}
		System.err.println(String.format("Unable to get the generated id for the object %s, setting it to 0",
				ps.getMetaData().getTableName(1)));
		return 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IAccountEntryLineDao#setAccountEntryStatus(int,
	 * int, java.lang.String)
	 */
	@Override
	public void setAccountEntryStatus(Long id, int newStatus, String auditLog) {

		initTemplate();
		template.update("UPDATE accountEntries SET status=? WHERE id=?", newStatus, id);
		if (auditLog == null) {
			if (newStatus == 0) {
				auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
						"UPDATED transaction entry to account: " + id, "accountEntries", id));
			}
		} else if (!"".equals(auditLog)) {
			auditDao.insertAuditRecord(
					new AuditLog(0l, userDao.getCurrentUser(), null, auditLog, "accountEntries", id));
		}

		// System.err.println("id=" + id + " status" + newStatus);
		// String sql = "UPDATE accountEntries SET status=? WHERE id=?;";
		// try (Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement(sql,
		// Statement.RETURN_GENERATED_KEYS)) {
		// ps.setInt(1, newStatus);
		// ps.setLong(2, id);
		// ps.executeUpdate();
		// if (auditLog == null) {
		// if (newStatus == 0) {
		// auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(),
		// null,
		// "UPDATED transaction entry to account: " + id, "accountEntries",
		// id));
		// }
		// } else if (!"".equals(auditLog)) {
		// auditDao.insertAuditRecord(
		// new AuditLog(0l, userDao.getCurrentUser(), null, auditLog,
		// "accountEntries", id));
		// }
		// ps.close();
		// connection.close();
		// } catch (SQLException ex) {
		// ex.printStackTrace();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IAccountEntryLineDao#deleteAccountEntry(int)
	 */
	@Override
	public void deleteAccountEntry(Long id) {
		initTemplate();
		String SQL = "DELETE FROM accountEntries WHERE id = ?";

		try {
			template.update(SQL, id);
		} catch (RuntimeException runtimeException) {

			System.err.println(runtimeException);
			throw runtimeException;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IAccountEntryLineDao#hasAccountEntry(java.lang.
	 * String, double, double, java.lang.String)
	 */
	@Override
	public boolean hasAccountEntry(String date, double withdrawal, double deposit, String desc) {

		initTemplate();
		String sql = "Select * from accountEntries where dateCol=? and withdrawalCol=? and depositCol=? and descriptionCol=?";
		List<AccountEntryLine> list = template.query(sql, mapper, date, withdrawal, deposit, desc);
		if (list.isEmpty()) {
			return false;
		} else {
			return true;
		}
		// try (Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement(sql)) {
		// ps.setString(1, date);
		// ps.setDouble(2, withdrawal);
		// ps.setDouble(3, deposit);
		// ps.setString(4, desc);
		// ResultSet rs = ps.executeQuery();
		// if (rs.next()) {
		// hasRecord = true;
		// }
		// ps.close();
		// connection.close();
		// } catch (SQLException x) {
		// x.printStackTrace();
		// }
		// return hasRecord;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IAccountEntryLineDao#saveAccount(com.quickveggies.
	 * entities.Account)
	 */
	@Override
	public void saveAccount(Account item) throws SQLException {
		initInsert();
		String accountName = item.getAccountName();
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("acc_name", item.getAccountName());
		args.put("acc_type", item.getAccountType());
		args.put("acc_number", item.getAccountNumber());
		args.put("bank_name", item.getBankName());
		args.put("initBalance", item.getInitBalance());
		args.put("phone", item.getPhone());
		args.put("description", item.getDescription());
		args.put("lastupdated", item.getLastupdated());

		Long id = insertAccount.executeAndReturnKey(args).longValue();

		auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
				"ADDED bank account ".concat(accountName), "accounts", id));

		/*
		 * String accountNumber = account.getAccountNumber(); int accountType =
		 * account.getAccountType(); double balance = account.getBalance();
		 * double initBalance = account.getInitBalance(); String accountName =
		 * account.getAccountName(); String phone = account.getPhone(); String
		 * description = account.getDescription(); String bankName =
		 * account.getBankName(); int lastupdated = account.getLastupdated(); //
		 * Statement execStmt = connection.createStatement(); //
		 * execStmt.execute("SET IDENTITY_INSERT banks ON"); Connection
		 * connection = dataSource.getConnection(); PreparedStatement statement
		 * = connection.prepareStatement(
		 * "INSERT INTO accounts (acc_name,acc_type,acc_number,bank_name,balance,initBalance,phone,description,lastupdated) VALUES(?,?,?,?,?,?,?,?,?)"
		 * , Statement.RETURN_GENERATED_KEYS); statement.setString(1,
		 * accountName); statement.setInt(2, accountType);
		 * statement.setString(3, accountNumber); statement.setString(4,
		 * bankName); statement.setDouble(5, balance); statement.setDouble(6,
		 * initBalance); statement.setString(7, phone); statement.setString(8,
		 * description); statement.setInt(9, lastupdated);
		 * statement.executeUpdate(); Integer key = getGeneratedKey(statement);
		 * auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(),
		 * null, "ADDED bank account ".concat(accountName), "accounts", key));
		 * statement.close(); connection.close();
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IAccountEntryLineDao#updateAccount(com.quickveggies.
	 * entities.Account)
	 */
	@Override
	public void updateAccount(Account account) {

		initTemplate();
		String tableName = "accounts";

		String sqlCommand = "UPDATE accounts SET lastupdated=?, acc_type=?, "
				+ "bank_name=?, acc_name=?, acc_number=?, phone=?, description=?, "
				+ "initBalance=?, balance=? WHERE id=?";
		template.update(sqlCommand, account.getLastupdated(), account.getAccountType(), account.getBankName(),
				account.getAccountName(), account.getAccountNumber(), account.getPhone(), account.getDescription(),
				account.getInitBalance(), account.getBalance(), account.getId());

		String auditMsg = "Added new entries in bank account " + account.getBankName();
		auditDao.insertAuditRecord(
				new AuditLog(account.getId(), userDao.getCurrentUser(), null, auditMsg, tableName, account.getId()));

		// String tableName = "accounts";
		// String sqlCommand = "UPDATE " + tableName + " SET lastupdated=?,
		// acc_type=?, "
		// + "bank_name=?, acc_name=?, acc_number=?, phone=?, description=?, "
		// + "initBalance=?, balance=? WHERE id=?";
		// try {
		// System.out.println(sqlCommand);
		// Connection connection = dataSource.getConnection();
		// PreparedStatement statement =
		// connection.prepareStatement(sqlCommand);
		// statement.setInt(1, account.getLastupdated());
		// statement.setInt(2, account.getAccountType());
		// statement.setString(3, account.getBankName());
		// statement.setString(4, account.getAccountName());
		// statement.setString(5, account.getAccountNumber());
		// statement.setString(6, account.getPhone());
		// statement.setString(7, account.getDescription());
		// statement.setDouble(8, account.getInitBalance());
		// statement.setDouble(9, account.getBalance());
		// statement.setLong(10, account.getId());
		// statement.executeUpdate();
		// // String auditMsg = String.format("UPDATED account (Entry No: %d
		// // )", account.getId());
		// // insertAuditRecord(new AuditLog(account.getId(), getCurrentUser(),
		// // null, auditMsg, tableName, account.getId()));
		// String auditMsg = "Added new entries in bank account " +
		// account.getBankName();
		// auditDao.insertAuditRecord(new AuditLog(account.getId(),
		// userDao.getCurrentUser(), null, auditMsg,
		// tableName, account.getId()));
		// statement.close();
		// connection.close();
		// } catch (SQLException e) {
		// System.out.print("sql exception in updateAccount " + e.getMessage());
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IAccountEntryLineDao#getAccountByName(java.lang.
	 * String)
	 */
	public Account getAccountByName(String accountName) throws SQLException {
		initTemplate();
		String sql = "SELECT * FROM accounts WHERE acc_name=?";
		final Account account = new Account();
		template.query(sql, new Object[] { accountName }, new RowCallbackHandler() {
			public void processRow(ResultSet set) throws SQLException {

				account.setAccountNumber(set.getString("acc_number"));
				account.setAccountType(set.getInt("acc_type"));
				account.setBalance(set.getDouble("balance"));
				account.setInitBalance(set.getDouble("initBalance"));
				account.setPhone(set.getString("phone"));
				account.setDescription(set.getString("description"));
				account.setBankName(set.getString("bank_name"));
				account.setLastupdated(set.getInt("lastupdated"));

			}
		});
		return account;

	}

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IAccountEntryLineDao#getAccountById(int)
	 */
	@Override
	public Account getAccountById(Long id) throws SQLException {

		initTemplate();
		List<Account> list = template.query("SELECT * FROM accounts WHERE id=?", mapperAccount, id);

		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/*
	 * public void setDataSource(DataSource dataSource) { this.dataSource =
	 * dataSource;
	 * 
	 * template = new JdbcTemplate(dataSource); }
	 * 
	 * private void initTemplate() { if (template == null) { template = new
	 * JdbcTemplate(dataSource); } }
	 */

}
