package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AuditLog;
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
    private SupplierDao supplierDao ;
	@Autowired
    private DSalesTableDao dSalesDao ;
	@Autowired
    private AuditDao auditDao;
	@Autowired
    private UserUtils userDao; 
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getExpenseEntryLineFromSql(int)
	 */
    @Override
	public String[] getExpenseEntryLineFromSql(int id) throws SQLException, NoSuchElementException {
        ResultSet set = getResult("select * from expenses where id='" + id + "';");
        String amount, date, comment, billto, type;
        if (set.next()) {
            amount = set.getString("amount");
            date = set.getString("date");
            comment = set.getString("comment");
            billto = set.getString("billto");
            type = set.getString("type");
            return new String[]{"" + id, amount, date, comment, billto, type};
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

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getLastExpenseType(int)
	 */
	@Override
	public String getLastExpenseType(int id) throws SQLException, NoSuchElementException {
        ResultSet set = getResult("select * from last_expenses where id='" + id + "';");
        if (set.next()) {
            return set.getString("type");
        } else {
            throw new NoSuchElementException();
        }
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getLastExpense(int)
	 */
    @Override
	public String getLastExpense(int id) throws SQLException, NoSuchElementException {
        ResultSet set = getResult("select * from last_expenses where id='" + id + "';");
        if (set.next()) {
            return set.getString("category");
        } else {
            throw new NoSuchElementException();
        }
    }
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#updateExpenseInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public void updateExpenseInfo(String name, String type, String defaultAmount) {

        try (PreparedStatement ps = dataSource.getConnection()
                .prepareStatement("UPDATE expenseInfo SET type=?,  defaultAmount=?  WHERE name = ?")) {
            ps.setString(1, type);
            ps.setString(2, defaultAmount);
            ps.setString(3, name);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "UPDATED exepense info :".concat(name), null, 0));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        addExpenseInfo(name, type, defaultAmount);
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#updateBuyerExpenseInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public void updateBuyerExpenseInfo(String name, String type, String defaultAmount) {

        try (PreparedStatement ps = dataSource.getConnection()
                .prepareStatement("UPDATE buyerExpenseInfo SET type=?,  defaultAmount=?  WHERE name = ?")) {
            ps.setString(1, type);
            ps.setString(2, defaultAmount);
            ps.setString(3, name);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "UPDATED buyer exepense info :".concat(name), null, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        addBuyerExpenseInfo(name, type, defaultAmount);
    }

    private static final String INSERT_EXPENSE_INFO_QRY = "IF NOT EXISTS (SELECT * FROM expenseInfo  WHERE name = ?)  INSERT INTO expenseInfo  (name, type, defaultAmount )  VALUES (?,?,?)";

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#addExpenseInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public void addExpenseInfo(String name, String type, String defaultAmount) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(INSERT_EXPENSE_INFO_QRY)) {
            ps.setString(1, name);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, defaultAmount);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "ADDED exepense info :".concat(name), null, 0));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static final String INSERT_BUYER_EXPENSE_INFO_QRY = "IF NOT EXISTS (SELECT * FROM buyerExpenseInfo  WHERE name = ?)  INSERT INTO buyerExpenseInfo  (name, type, defaultAmount )  VALUES (?,?,?)";

    public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#addBuyerExpenseInfo(java.lang.String, java.lang.String, java.lang.String)
	 */
    @Override
	public void addBuyerExpenseInfo(String name, String type, String defaultAmount) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(INSERT_BUYER_EXPENSE_INFO_QRY)) {
            ps.setString(1, name);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, defaultAmount);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "ADDED buyerExpenseInfo info :".concat(name), null, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getExpenseInfoFor(java.lang.String)
	 */
    @Override
	public ExpenseInfo getExpenseInfoFor(String name) {
        ExpenseInfo ei = new ExpenseInfo();
        try {
            PreparedStatement ps = dataSource.getConnection().prepareStatement("Select * FROM expenseInfo  WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ei.setId(rs.getInt("id"));
                ei.setName(rs.getString("name"));
                ei.setType(rs.getString("type"));
                ei.setDefaultAmount(rs.getString("defaultAmount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ei;
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getBuyerExpenseInfoFor(java.lang.String)
	 */
    @Override
	public ExpenseInfo getBuyerExpenseInfoFor(String name) {
        ExpenseInfo ei = new ExpenseInfo();
        try {
            PreparedStatement ps = dataSource.getConnection().prepareStatement("Select * FROM buyerExpenseInfo  WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ei.setId(rs.getInt("id"));
                ei.setName(rs.getString("name"));
                ei.setType(rs.getString("type"));
                ei.setDefaultAmount(rs.getString("defaultAmount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ei;
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getExpenseInfoList()
	 */
    @Override
	public List<ExpenseInfo> getExpenseInfoList() {
        List<ExpenseInfo> list = new ArrayList<>();
        try {
            ResultSet rs = getResult("Select * from expenseInfo  order by name");
            while (rs.next()) {
                ExpenseInfo ei = new ExpenseInfo();
                ei.setId(rs.getInt("id"));
                ei.setName(rs.getString("name"));
                ei.setType(rs.getString("type"));
                ei.setDefaultAmount(rs.getString("defaultAmount"));
                list.add(ei);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#getBuyerExpenseInfoList()
	 */
    @Override
	public List<ExpenseInfo> getBuyerExpenseInfoList() {
        List<ExpenseInfo> list = new ArrayList<>();
        try {
            ResultSet rs = getResult("Select * from buyerExpenseInfo order by name");
            while (rs.next()) {
                ExpenseInfo ei = new ExpenseInfo();
                ei.setId(rs.getInt("id"));
                ei.setName(rs.getString("name"));
                ei.setType(rs.getString("type"));
                ei.setDefaultAmount(rs.getString("defaultAmount"));
                list.add(ei);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenseDao#deleteExpenseInfo(java.lang.String)
	 */
    @Override
	public void deleteExpenseInfo(String name) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement("DELETE FROM expenseInfo WHERE name = ?")) {
            ps.setString(1, name);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "DELETED exepense info :".concat(name), null, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


  
    


}
