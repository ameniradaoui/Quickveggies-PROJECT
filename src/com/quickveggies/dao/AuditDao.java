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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AccountEntryPayment;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IUserUtils;
@Component
public class AuditDao implements IAuditDao {
	

	
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
		insert = new SimpleJdbcInsert(dataSource).withTableName("auditLog").usingGeneratedKeyColumns("id");
	}

	
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();
		
	}
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


	
	
	private static RowMapper<AuditLog> mapper = new RowMapper<AuditLog>() {
		@Override
		public AuditLog mapRow(ResultSet data, int index) throws SQLException {
			AuditLog item = new AuditLog(
			data.getLong("id"),
			data.getString("userId"),
			data.getDate("eventtime"),
			data.getString("eventDetail"),
			data.getString("eventObject"),
			data.getLong("eventObjectId")){{
			setOldValues(data.getString("oldValues"));
			setNewValues(data.getString("newValues"));
			setEventDetail(data.getString("eventDetail"));
		    setName(data.getString("name"));
			setDate(data.getDate("date")== null ? null : new Date(data.getDate("date").getTime()));
			setAmount(data.getDouble("amount")); 
			}};
			
			return item;
		}
	};

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IAuditDao#getAuditRecords()
	 */
	@Override
	public List<AuditLog> getAuditRecords() {
		
		initTemplate();
	     return  template.query("SELECT * FROM auditLog ", mapper);
	     
	     
		
//	        String sql = "SELECT * FROM auditLog";
//	        List<AuditLog> list = new ArrayList<>();
//	        try {
//	            ResultSet rs = getResult(sql);
//	            while (rs.next()) {
//	                AuditLog log = new AuditLog(rs.getLong(1), rs.getString("userId"),
//	                        rs.getDate("eventtime"),
//	                        rs.getString("eventDetail"), rs.getString("eventObject"),
//	                        rs.getLong("eventObjectId")) {{
//	                            setOldValues(rs.getString("oldValues"));
//	                            setNewValues(rs.getString("newValues"));
//	                            setName(rs.getString("name"));
//	                            setDate(rs.getDate("date") == null ? null : new Date(rs.getDate("date").getTime()));
//	                            setAmount(rs.getDouble("amount"));
//	                        }};
//	                list.add(log);
//	                rs.close();
//	            }
//
//	        } catch (SQLException ex) {
//	            ex.printStackTrace();
//	        }
//	        return list;
	    }
	@Autowired
	 private static  UserUtils userUtils;
	@Autowired
	 private static AuditDao auditDao;
	 
	 public AuditDao() {
		super();
		// TODO Auto-generated constructor stub
	}


//	private ResultSet getResult(String query) throws SQLException {
//	        Statement statement =  dataSource.getConnection().createStatement();
//	        ResultSet resultSet = statement.executeQuery(query);
//	        return resultSet;
//	    }

	   
	  /* (non-Javadoc)
	 * @see com.quickveggies.dao.IAuditDao#insertAuditRecord(com.quickveggies.entities.AuditLog)
	 */
	public void insertAuditRecord(AuditLog item) {
		initInsert();
		
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("userId", item.getUserId());
		args.put("eventDetail", item.getEventDetail());
		args.put("eventObject", item.getEventObject());
		args.put("eventObjectId", item.getEventObjectId());
		args.put("oldValues", item.getOldValues());
		args.put("newValues", item.getNewValues());
		args.put("name", item.getName());
		args.put("date", item.getDate() == null ? null : new java.sql.Timestamp(item.getDate().getTime()));
		args.put("amount", item.getAmount() == null ? 0.0 : item.getAmount());
		
	
		
		
//		
//	        String sql = "INSERT INTO auditLog (userId, eventDetail, eventObject, "
//	                + " eventObjectId, oldValues, newValues, name, date, amount) "
//	                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
//		try ( Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(sql);) {
//	            ps.setString(1, log.getUserId());
//	            ps.setString(2, log.getEventDetail());
//	            ps.setString(3, log.getEventObject());
//	            ps.setLong(4, log.getEventObjectId());
//	            ps.setString(5, log.getOldValues());
//	            ps.setString(6, log.getNewValues());
//	            ps.setString(7, log.getName());
//	            ps.setTimestamp(8, log.getDate() == null ? null : new java.sql.Timestamp(log.getDate().getTime()));
////	            Date(8, log.getDate() == null ? null : new java.sql.Date(log.getDate().getTime()));
//	            ps.setDouble(9, log.getAmount() == null ? 0.0 : log.getAmount());
//	            //ps.executeUpdate();
//	            ps.execute();
//	            ps.close();
//	            connection.close();
//
//	        } catch (SQLException ex) {
//	            ex.printStackTrace();
//	        }
	    }

	  static void auditEntry(String baseMsg, String tableName, String[] values, Long generatedId) {
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
	       
		auditDao.insertAuditRecord(new AuditLog(0l, userUtils.getCurrentUser(), null, auditMsg, tableName, generatedId));
	    }
	


}
