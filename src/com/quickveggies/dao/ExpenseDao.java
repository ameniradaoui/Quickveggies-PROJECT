package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Company;
import com.quickveggies.entities.ExpenseInfo;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.IExpenseDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;

@Component
public class ExpenseDao implements IExpenseDao {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private BuyerDao bd;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private DSalesTableDao dSalesDao;
	@Autowired
	private AuditDao auditDao;
	@Autowired
	private UserUtils userDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#getExpenseEntryLineFromSql(int)
	 */

	private JdbcTemplate template;

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);

		}
	}

	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insert == null) {
			createInsert();

		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("expenseInfo").usingGeneratedKeyColumns("id");
	}
	
	
	private SimpleJdbcInsert insertBuyerExp;

	private void initInsertBuyerExp() {
		if (insertBuyerExp == null) {
			createInsertBuyerExp();

		}
	}

	private void createInsertBuyerExp() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("buyerExpenseInfo").usingGeneratedKeyColumns("id");
	}

	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();

	}

	private static RowMapper<ExpenseInfo> Mapper = new RowMapper<ExpenseInfo>() {
		@Override
		public ExpenseInfo mapRow(ResultSet data, int index) throws SQLException {
			ExpenseInfo item = new ExpenseInfo();
			item.setId(data.getLong("id"));
			item.setName(data.getString("name"));
			item.setType(data.getString("type"));
			item.setDefaultAmount(data.getString("defaultAmount"));

			return item;
		}
	};

	// EXPENSES DOES NOT EXIST ON DATABASE
	@Override
	public String[] getExpenseEntryLineFromSql(Long id) throws SQLException, NoSuchElementException {
		ResultSet set = getResult("select * from expenses where id='" + id + "';");
		String amount, date, comment, billto, type;
		if (set.next()) {
			amount = set.getString("amount");
			date = set.getString("date");
			comment = set.getString("comment");
			billto = set.getString("billto");
			type = set.getString("type");
			set.close();
			return new String[] { "" + id, amount, date, comment, billto, type };
		} else {
			throw new NoSuchElementException();
		}
	}

	private ResultSet getResult(String query) throws SQLException {
		Statement statement = dataSource.getConnection().createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		return resultSet;
	}

	public ExpenseDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#getLastExpenseType(int)
	 */
	
	@Override
	public String getLastExpenseType(Long id) throws SQLException, NoSuchElementException {
		ResultSet set = getResult("select * from last_expenses where id='" + id + "';");
		set.close();
		if (set.next()) {
			return set.getString("type");
		} else {
			throw new NoSuchElementException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#getLastExpense(int)
	 */
	
	@Override
	public String getLastExpense(Long id) throws SQLException, NoSuchElementException {
		ResultSet set = getResult("select * from last_expenses where id='" + id + "';");
		if (set.next()) {
			set.close();
			return set.getString("category");
		} else {
			throw new NoSuchElementException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#updateExpenseInfo(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateExpenseInfo(String name, String type, String defaultAmount) {

		template.update("UPDATE expenseInfo SET type=?,  defaultAmount=?  WHERE name = ?", type, defaultAmount ,name  );
		auditDao.insertAuditRecord(
				new AuditLog(0l, userDao.getCurrentUser(), null, "UPDATED exepense info :".concat(name), null, 0l));
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection
//						.prepareStatement("UPDATE expenseInfo SET type=?,  defaultAmount=?  WHERE name = ?")) {
//			ps.setString(1, type);
//			ps.setString(2, defaultAmount);
//			ps.setString(3, name);
//			ps.executeUpdate();
//			auditDao.insertAuditRecord(
//					new AuditLog(0l, userDao.getCurrentUser(), null, "UPDATED exepense info :".concat(name), null, 0l));
//			ps.close();
//			connection.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		addExpenseInfo(name, type, defaultAmount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IExpenseDao#updateBuyerExpenseInfo(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void updateBuyerExpenseInfo(String name, String type, String defaultAmount) {
		
		template.update("UPDATE buyerExpenseInfo SET type=?,  defaultAmount=?  WHERE name = ?", type, defaultAmount ,name  );
		auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
				"UPDATED buyer exepense info :".concat(name), null, 0l));
	//	try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection
//						.prepareStatement("UPDATE buyerExpenseInfo SET type=?,  defaultAmount=?  WHERE name = ?")) {
//			ps.setString(1, type);
//			ps.setString(2, defaultAmount);
//			ps.setString(3, name);
//			ps.executeUpdate();
//			auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
//					"UPDATED buyer exepense info :".concat(name), null, 0l));
//			ps.close();
//			connection.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		addBuyerExpenseInfo(name, type, defaultAmount);
	}

	private static final String INSERT_EXPENSE_INFO_QRY = "IF NOT EXISTS (SELECT * FROM expenseInfo  WHERE name = ?)  INSERT INTO expenseInfo  (name, type, defaultAmount )  VALUES (?,?,?)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#addExpenseInfo(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	
	private Long createExpenseInfo(ExpenseInfo item){
		initInsert();
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("name", item.getName());
		args.put("type", item.getType());
		args.put("defaultamount",item.getDefaultAmount());
		Long id = insert.executeAndReturnKey(args).longValue();
		return id;
	}
	
	private Long createExpenseInfoBuyer(ExpenseInfo item){
		initInsertBuyerExp();
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("name", item.getName());
		args.put("type", item.getType());
		args.put("defaultamount",item.getDefaultAmount());
		Long id = insertBuyerExp.executeAndReturnKey(args).longValue();
		return id;
	}

	@Override
	public void addExpenseInfo(String name, String type, String defaultAmount) {
		
		Long id = null;
		
		Long count=template.query("SELECT count(*) FROM expenseInfo  WHERE name = ?", SingleColumnRowMapper.newInstance(Long.class) , name).get(0);
		if(count.equals(0l)){
			ExpenseInfo expenseInfo=new ExpenseInfo();
			expenseInfo.setName(name);
			expenseInfo.setType(type);
			expenseInfo.setDefaultAmount(defaultAmount);
			id=createExpenseInfo(expenseInfo);
			auditDao.insertAuditRecord(
					new AuditLog(0l, userDao.getCurrentUser(), null, "ADDED exepense info :".concat(name), null, 0l));
		
		}
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(INSERT_EXPENSE_INFO_QRY)) {
//			ps.setString(1, name);
//			ps.setString(2, name);
//			ps.setString(3, type);
//			ps.setString(4, defaultAmount);
//			ps.executeUpdate();
//			auditDao.insertAuditRecord(
//					new AuditLog(0l, userDao.getCurrentUser(), null, "ADDED exepense info :".concat(name), null, 0l));
//			ps.close();
//			connection.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}

	private static final String INSERT_BUYER_EXPENSE_INFO_QRY = "IF NOT EXISTS (SELECT * FROM buyerExpenseInfo  WHERE name = ?)  INSERT INTO buyerExpenseInfo  (name, type, defaultAmount )  VALUES (?,?,?)";

	public DataSource getDataSource() {
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IExpenseDao#addBuyerExpenseInfo(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	

	@Override
	public void addBuyerExpenseInfo(String name, String type, String defaultAmount) {
		

		Long id = null;
		
		Long count=template.query("SELECT count(*) FROM buyerExpenseInfo  WHERE name = ?", SingleColumnRowMapper.newInstance(Long.class) , name).get(0);
		if(count.equals(0l)){
			ExpenseInfo expenseInfo=new ExpenseInfo();
			expenseInfo.setName(name);
			expenseInfo.setType(type);
			expenseInfo.setDefaultAmount(defaultAmount);
			id=createExpenseInfoBuyer(expenseInfo);
			auditDao.insertAuditRecord(
					new AuditLog(0l, userDao.getCurrentUser(), null, "ADDED exepense info :".concat(name), null, 0l));
		
		}
		
		
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(INSERT_BUYER_EXPENSE_INFO_QRY)) {
//			ps.setString(1, name);
//			ps.setString(2, name);
//			ps.setString(3, type);
//			ps.setString(4, defaultAmount);
//			ps.executeUpdate();
//			auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
//					"ADDED buyerExpenseInfo info :".concat(name), null, 0l));
//			ps.close();
//			connection.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#getExpenseInfoFor(java.lang.String)
	 */
	@Override
	public ExpenseInfo getExpenseInfoFor(String name) {

		List<ExpenseInfo> list = template.query("Select * FROM expenseInfo  WHERE name = ?", Mapper, name);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
		// ExpenseInfo ei = new ExpenseInfo();
		// try {
		// Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement("Select * FROM
		// expenseInfo WHERE name = ?");
		// ps.setString(1, name);
		// ResultSet rs = ps.executeQuery();
		// if (rs.next()) {
		// ei.setId(rs.getLong("id"));
		// ei.setName(rs.getString("name"));
		// ei.setType(rs.getString("type"));
		// ei.setDefaultAmount(rs.getString("defaultAmount"));
		// }
		// ps.close();
		// connection.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// return ei;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IExpenseDao#getBuyerExpenseInfoFor(java.lang.String)
	 */
	@Override
	public ExpenseInfo getBuyerExpenseInfoFor(String name) {

		List<ExpenseInfo> list = template.query("Select * FROM buyerExpenseInfo  WHERE name = ?", Mapper, name);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}

		// ExpenseInfo ei = new ExpenseInfo();
		// try {
		// Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement("Select * FROM
		// buyerExpenseInfo WHERE name = ?");
		// ps.setString(1, name);
		// ResultSet rs = ps.executeQuery();
		// if (rs.next()) {
		// ei.setId(rs.getLong("id"));
		// ei.setName(rs.getString("name"));
		// ei.setType(rs.getString("type"));
		// ei.setDefaultAmount(rs.getString("defaultAmount"));
		//
		// }
		// ps.close();
		// connection.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		//
		// return ei;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#getExpenseInfoList()
	 */
	@Override
	public List<ExpenseInfo> getExpenseInfoList() {
		initTemplate();
		return template.query("Select * from expenseInfo  order by name", Mapper);

		// List<ExpenseInfo> list = new ArrayList<>();
		// try {
		// ResultSet rs = getResult("Select * from expenseInfo order by name");
		// while (rs.next()) {
		// ExpenseInfo ei = new ExpenseInfo();
		// ei.setId(rs.getLong("id"));
		// ei.setName(rs.getString("name"));
		// ei.setType(rs.getString("type"));
		// ei.setDefaultAmount(rs.getString("defaultAmount"));
		// list.add(ei);
		//
		// }
		// rs.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		//
		// return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#getBuyerExpenseInfoList()
	 */
	@Override
	public List<ExpenseInfo> getBuyerExpenseInfoList() {
		
		initTemplate();
		return template.query("Select * from buyerExpenseInfo order by name", Mapper);
		// List<ExpenseInfo> list = new ArrayList<>();
		// try {
		// ResultSet rs = getResult("Select * from buyerExpenseInfo order by
		// name");
		// while (rs.next()) {
		// ExpenseInfo ei = new ExpenseInfo();
		// ei.setId(rs.getLong("id"));
		// ei.setName(rs.getString("name"));
		// ei.setType(rs.getString("type"));
		// ei.setDefaultAmount(rs.getString("defaultAmount"));
		// list.add(ei);
		// }
		// rs.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IExpenseDao#deleteExpenseInfo(java.lang.String)
	 */
	@Override
	public void deleteExpenseInfo(String name) {

		String SQL = "DELETE FROM expenseInfo WHERE name = ?";

		try {
			template.update(SQL, name);
			auditDao.insertAuditRecord(
					new AuditLog(0l, userDao.getCurrentUser(), null, "DELETED exepense info :".concat(name), null, 0l));

		} catch (RuntimeException runtimeException) {

			System.err.println(runtimeException);
			throw runtimeException;
		}

		// try (Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement("DELETE FROM
		// expenseInfo WHERE name = ?")) {
		// ps.setString(1, name);
		// ps.executeUpdate();
		// auditDao.insertAuditRecord(
		// new AuditLog(0l, userDao.getCurrentUser(), null, "DELETED exepense
		// info :".concat(name), null, 0l));
		// ps.close();
		// connection.close();
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}

}
