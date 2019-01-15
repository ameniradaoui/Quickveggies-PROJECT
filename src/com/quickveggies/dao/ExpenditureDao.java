package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ai.util.dates.DateUtil;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Expenditure;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.IExpenditureDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class ExpenditureDao implements IExpenditureDao {
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private BuyerDao bd ;
	@Autowired
    private SupplierDao supplierDao ;
	@Autowired
    private DatabaseClient db ;
	@Autowired
    private DSalesTableDao dSalesDao;
	@Autowired
    private AuditDao auditDao ;
	@Autowired
	private UserUtils userDao; 
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#deleteExpenditureType(java.lang.String)
	 */
	@Override
	public void deleteExpenditureType(String name) {
        final String sql = "DELETE FROM expenditureType WHERE name = ?";
        try ( Connection connection = dataSource.getConnection();
        		final PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "DELETED expenditure type:".concat(name), null, 0));
            connection.close();       
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	  private static final String INSERT_EXPENDITURE_TYPE_QRY = "IF NOT EXISTS (SELECT * FROM expenditureType WHERE name = ?)  INSERT INTO expenditureType (name)  VALUES (?)";

    public ExpenditureDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#addExpenditureType(java.lang.String)
	 */
	@Override
	public void addExpenditureType(String name) {
        try ( Connection connection = dataSource.getConnection();
        		final PreparedStatement ps = connection.prepareStatement(INSERT_EXPENDITURE_TYPE_QRY)) {
            ps.setString(1, name);
            ps.setString(2, name);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "ADDED expenditure type:".concat(name), null, 0));
            connection.close();       
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#getExpenditureTypeList()
	 */
    @Override
	public List<String> getExpenditureTypeList() {
        final String sql = "SELECT * FROM expenditureType";
        List<String> list = new ArrayList<>();
        try {
            ResultSet rs = userDao.getResult(sql);
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    private static final String INSERT_EXPENDITURE_QRY = "INSERT INTO expenditures  ("
            + "amount ,date , comment , billto , type, receipt) VALUES (?, ?, ?, ?, ?, ?); ";

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#addExpenditure(com.quickveggies.entities.Expenditure)
	 */
    @Override
	public boolean addExpenditure(Expenditure xpr) {
        String sql = INSERT_EXPENDITURE_QRY;
        try ( Connection connection = dataSource.getConnection();
        		PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, xpr.getAmount());
            ps.setString(2, xpr.getDate());
            ps.setString(3, xpr.getComment());
            ps.setString(4, xpr.getPayee());
            ps.setString(5, xpr.getType());
            ps.setBlob(6, xpr.getReceipt());
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null,
                    "Expenditure entry recorded in system",
                    "expenditures", db.getGeneratedKey(ps)));
            connection.close(); 
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#getExpenditureList()
	 */
    @Override
	public List<Expenditure> getExpenditureList() {
        String sql = "select * from expenditures;";
        List<Expenditure> list = new ArrayList<>();
        try {
            ResultSet rs = userDao.getResult(sql);
            while (rs.next()) {
                Expenditure xpr = new Expenditure();
                xpr.setAmount(rs.getString("amount"));
                xpr.setComment(rs.getString("comment"));
                xpr.setDate(rs.getString("date"));
                xpr.setId(rs.getInt("id"));
                xpr.setPayee(rs.getString("billto"));
                Blob blob = rs.getBlob("receipt");
                if (blob != null) {
                    xpr.setReceipt(blob.getBinaryStream());
                }
                xpr.setType(rs.getString("type"));
                list.add(xpr);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#getExpenditureById(int)
	 */
    @Override
	public Expenditure getExpenditureById(int id) {
        String sql = "SELECT * FROM expenditures WHERE id=?;";
        try ( Connection connection = dataSource.getConnection();
        		PreparedStatement ps =connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Expenditure xpr = new Expenditure();
                xpr.setAmount(rs.getString("amount"));
                xpr.setComment(rs.getString("comment"));
                xpr.setDate(rs.getString("date"));
                xpr.setId(rs.getInt("id"));
                xpr.setPayee(rs.getString("billto"));
                Blob blob = rs.getBlob("receipt");
                ps.close(); 
                if (blob != null) {
                    xpr.setReceipt(blob.getBinaryStream());
                }
                xpr.setType(rs.getString("type"));
                connection.close();
                return xpr;
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
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

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IExpenditureDao#deleteExpenditureEntry(int, boolean)
	 */
    @Override
	public void deleteExpenditureEntry(int id, boolean writeAuditLog) {
        try {
            String tableName = "expenditures";
            String deleteCommand;
            Expenditure expenditure = getExpenditureById(id);
            if (expenditure == null) {
                return;
            }
            deleteCommand = "delete from " + tableName + " where id=?;";
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(deleteCommand);
            statement.setInt(1, id);
            statement.execute();
            if (writeAuditLog) {
                auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null,
                        "DELETED Entry for " + TABLE_MAP.get(tableName) + " ("
                                + expenditure.getType() + ")", null, 0) {{
                                    setName(expenditure.getPayee());
                                    try {
                                        String format = DateUtil.determineDateFormat(expenditure.getDate());
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                                        setDate(Date.from(LocalDate.parse(expenditure.getDate(), formatter)
                                                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                                    }
                                    catch (Exception x) {
                                        setDate(null);
                                        x.printStackTrace();
                                    }
                                    setAmount(Double.parseDouble(expenditure.getAmount()));
                                }});
                connection.close();
            }
        }
        catch (SQLException e) {
            System.out.print("sql exception in deleteTableEntries " + e.getMessage());
        }
    }

	
    

}
