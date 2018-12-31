package com.quickveggies.dao;

import static com.quickveggies.entities.Buyer.COLD_STORE_BUYER_TITLE;
import static com.quickveggies.entities.Buyer.GODOWN_BUYER_TITLE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import com.quickveggies.entities.AuditLog;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IUserUtils;
@Component
public class AuditDao implements IAuditDao {
	

	 private static final String STORAGE_BUYER_DEALS = "storagebuyerdeals";
	
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

	@Autowired
	 private DataSource dataSource;
	
	 
	 public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IAuditDao#getAuditRecords()
	 */
	@Override
	public List<AuditLog> getAuditRecords() {
	        String sql = "SELECT * FROM auditLog";
	        List<AuditLog> list = new ArrayList<>();
	        try {
	            ResultSet rs = getResult(sql);
	            while (rs.next()) {
	                AuditLog log = new AuditLog(rs.getInt(1), rs.getString("userId"),
	                        rs.getDate("eventtime"),
	                        rs.getString("eventDetail"), rs.getString("eventObject"),
	                        rs.getInt("eventObjectId")) {{
	                            setOldValues(rs.getString("oldValues"));
	                            setNewValues(rs.getString("newValues"));
	                            setName(rs.getString("name"));
	                            setDate(rs.getDate("date") == null ? null : new Date(rs.getDate("date").getTime()));
	                            setAmount(rs.getDouble("amount"));
	                        }};
	                list.add(log);
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return list;
	    }
	@Autowired
	 private static  UserUtils userUtils;
	@Autowired
	 private static AuditDao auditDao;
	 
	 public AuditDao() {
		super();
		// TODO Auto-generated constructor stub
	}


	private ResultSet getResult(String query) throws SQLException {
	        Statement statement =  dataSource.getConnection().createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        return resultSet;
	    }

	   
	  /* (non-Javadoc)
	 * @see com.quickveggies.dao.IAuditDao#insertAuditRecord(com.quickveggies.entities.AuditLog)
	 */
	public void insertAuditRecord(AuditLog log) {
	        String sql = "INSERT INTO auditLog (userId, eventDetail, eventObject, "
	                + " eventObjectId, oldValues, newValues, name, date, amount) "
	                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql);) {
	            ps.setString(1, log.getUserId());
	            ps.setString(2, log.getEventDetail());
	            ps.setString(3, log.getEventObject());
	            ps.setInt(4, log.getEventObjectId());
	            ps.setString(5, log.getOldValues());
	            ps.setString(6, log.getNewValues());
	            ps.setString(7, log.getName());
	            ps.setTimestamp(8, log.getDate() == null ? null : new java.sql.Timestamp(log.getDate().getTime()));
//	            Date(8, log.getDate() == null ? null : new java.sql.Date(log.getDate().getTime()));
	            ps.setDouble(9, log.getAmount() == null ? 0.0 : log.getAmount());
	            //ps.executeUpdate();
	            ps.execute();

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    }

	  static void auditEntry(String baseMsg, String tableName, String[] values, int generatedId) {
	        String formattedTableName = TABLE_MAP.get(tableName);
	        String auditMsg = null;
	        if (tableName.toLowerCase().equals("buyerdeals")) {
	            if (values[0].equalsIgnoreCase(COLD_STORE_BUYER_TITLE)) {
	                tableName = STORAGE_BUYER_DEALS;
	                auditMsg = String.format(baseMsg, formattedTableName + " from " + COLD_STORE_BUYER_TITLE, generatedId);
	            }
	            else if (values[0].equalsIgnoreCase(GODOWN_BUYER_TITLE)) {
	                tableName = STORAGE_BUYER_DEALS;
	                auditMsg = String.format(baseMsg, formattedTableName + " from " + GODOWN_BUYER_TITLE, generatedId);
	            }
	        }
	        else if (tableName.toLowerCase().equals("supplierdeals")) {
	            if (values[0].equalsIgnoreCase(COLD_STORE_BUYER_TITLE)) {
	                auditMsg = String.format(baseMsg, formattedTableName + " from " + COLD_STORE_BUYER_TITLE, generatedId);
	            }
	            else if (values[0].equalsIgnoreCase(GODOWN_BUYER_TITLE)) {
	                auditMsg = String.format(baseMsg, formattedTableName + " from " + GODOWN_BUYER_TITLE, generatedId);
	            }
	        }
	        if (auditMsg == null) {
	            auditMsg = String.format(baseMsg, formattedTableName, generatedId);
	        }
	       
		auditDao.insertAuditRecord(new AuditLog(0, userUtils.getCurrentUser(), null, auditMsg, tableName, generatedId));
	    }
	


}
