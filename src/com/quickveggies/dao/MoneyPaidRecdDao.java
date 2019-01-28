package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.swing.text.rtf.RTFEditorKit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.LadaanBijakSaleDeal;
import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.IMoneyPaidRecordDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;
import com.quickveggies.model.EntityType;

@Component
public class MoneyPaidRecdDao implements IMoneyPaidRecordDao {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private BuyerDao bd;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private DatabaseClient databaseClient;
	@Autowired
	private DSalesTableDao dSalesDao;
	@Autowired
	private AuditDao auditDao;
	@Autowired
	private UserUtils userDao;
	
	
	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insert == null) {
			createInsert();

		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("partyMoney").usingGeneratedKeyColumns("id");
	}
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IMoneyPaidRecordDao#addMoneyPaidRecdInfo(com.
	 * quickveggies.entities.MoneyPaidRecd)
	 */
	@Override
	public Long addMoneyPaidRecdInfo(MoneyPaidRecd mpr) {
		return this.addMoneyPaidRecdInfo(mpr, "");
	}

	private static final String QRY_PAID_RECD_MONEY_FOR_PARTY_ = "Select * from partyMoney where RTRIM(LTRIM(LOWER(partyType))) = ? ";
	private static final String QRY_PAID_RECD_MONEY_FOR_PARTY = "Select * from partyMoney where RTRIM(LTRIM(LOWER(partyType))) = ?";

	private static final String QRY_PAID_RECD_MONEY_FOR_TITLE = " AND RTRIM(LTRIM(LOWER(title))) = ? ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IMoneyPaidRecordDao#getMoneyPaidRecdList(java.lang.
	 * String, com.quickveggies.model.EntityType)
	 */
	
	
	private JdbcTemplate template;

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);

		}
	}



	private ResultSet getResult(String query) throws SQLException {
		Statement statement = dataSource.getConnection().createStatement();
		ResultSet resultSet = statement.executeQuery(query);

		return resultSet;
	}
	private static RowMapper<MoneyPaidRecd> mapper = new RowMapper<MoneyPaidRecd>() {
		@Override
		public MoneyPaidRecd mapRow(ResultSet rs, int index) throws SQLException {
		
			MoneyPaidRecd mpr = new MoneyPaidRecd();
			mpr.setDate(rs.getString("date"));
			mpr.setId(rs.getLong("id"));
			mpr.setPaid(rs.getString("paid"));
			mpr.setReceived(rs.getString("received"));
			mpr.setTitle(rs.getString("title"));
			mpr.setPartyType(rs.getString("partyType"));
			mpr.setDescription(rs.getString("description"));
			mpr.setPaymentMode(rs.getString("paymentMode"));
			mpr.setBankName(rs.getString("bankName"));
			mpr.setChequeNo(rs.getString("chequeNo"));
			mpr.setDepositDate(rs.getString("depositDate"));
			mpr.setTag(rs.getString("tag"));
			Blob blob = rs.getBlob("receipt");
			if (blob != null) {
				mpr.setReceipt(blob.getBinaryStream());
			}
			

			return mpr;
		}
	};
	
	
	@Override
	public List<MoneyPaidRecd> getMoneyPaidRecdList(String partyTitle, EntityType pType) {
		initTemplate();
	  // String sql = "Select * from partyMoney where RTRIM(LTRIM(LOWER(partyType))) = ?";
	   
		//List<MoneyPaidRecd> list = new ArrayList<>();
		String query = QRY_PAID_RECD_MONEY_FOR_PARTY + (partyTitle == null ? "" : QRY_PAID_RECD_MONEY_FOR_TITLE) + ";";
		return template.query(query, mapper , pType.getValue().trim().toLowerCase() ,partyTitle.toLowerCase().trim() );
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(query)) {
//			ps.setString(1, pType.getValue().trim().toLowerCase());
//			if (partyTitle != null) {
//				ps.setString(2, partyTitle.toLowerCase().trim());
//			}
//			ResultSet rs = ps.executeQuery();
//			addMprRsToList(rs, list);
//			ps.close();
//			connection.close();
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IMoneyPaidRecordDao#getAllMoneyPaidRecdList(com.
	 * quickveggies.model.EntityType)
	 */
	@Override
	public List<MoneyPaidRecd> getAllMoneyPaidRecdList(EntityType pType) {
		initTemplate();
		
		String sql = "Select * from partyMoney where RTRIM(LTRIM(LOWER(partyType))) = ?";
		return template.query(sql, mapper, pType.getValue().trim().toLowerCase());
		
//		List<MoneyPaidRecd> list = new ArrayList<>();
//
//		try {
//			Connection connection = dataSource.getConnection();
//			PreparedStatement ps = connection.prepareStatement(QRY_PAID_RECD_MONEY_FOR_PARTY);
//
//			ps.setString(1, pType.getValue().trim().toLowerCase());
//			ResultSet rs = ps.executeQuery();
//			addMprRsToList(rs, list);
//			ps.close();
//			connection.close();
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return list;
	}

	public MoneyPaidRecdDao() {
		super();
		// TODO Auto-generated constructor stub
	}

//	private void addMprRsToList(ResultSet rs, List<MoneyPaidRecd> list) throws SQLException {
//		while (rs.next()) {
//			MoneyPaidRecd mpr = new MoneyPaidRecd();
//			mpr.setDate(rs.getString("date"));
//			mpr.setId(rs.getLong("id"));
//			mpr.setPaid(rs.getString("paid"));
//			mpr.setReceived(rs.getString("received"));
//			mpr.setTitle(rs.getString("title"));
//			mpr.setPartyType(rs.getString("partyType"));
//			mpr.setDescription(rs.getString("description"));
//			mpr.setPaymentMode(rs.getString("paymentMode"));
//			mpr.setBankName(rs.getString("bankName"));
//			mpr.setChequeNo(rs.getString("chequeNo"));
//			mpr.setDepositDate(rs.getString("depositDate"));
//			mpr.setTag(rs.getString("tag"));
//			Blob blob = rs.getBlob("receipt");
//			if (blob != null) {
//				mpr.setReceipt(blob.getBinaryStream());
//			}
//			list.add(mpr);
//		}
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IMoneyPaidRecordDao#getAdvanceMoneyPaidList(com.
	 * quickveggies.model.EntityType)
	 */
	@Override
	public List<MoneyPaidRecd> getAdvanceMoneyPaidList(EntityType pType) {
		
		System.out.println(pType);
		initTemplate();
		return template.query("Select * from partyMoney where PartyType=? and isAdvanced='true' ", mapper , pType.getValue().trim().toLowerCase());
//		List<MoneyPaidRecd> list = new ArrayList<>();
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection
//						.prepareStatement("Select * from partyMoney where PartyType=? and isAdvanced='true' ")) {
//			ps.setString(1, pType.getValue().trim().toLowerCase());
//			ResultSet rs = ps.executeQuery();
//			addMprRsToList(rs, list);
//			ps.close();
//			connection.close();
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IMoneyPaidRecordDao#getMoneyPaidRecd(int)
	 */
	@Override
	public MoneyPaidRecd getMoneyPaidRecd(Long id) {
		initTemplate();
		 List<MoneyPaidRecd> list = template.query("Select * from partyMoney where id=?", mapper , id );
		 if (list.isEmpty()){
			 return null ;
		 }else {
			 return list.get(0);
		 }
		
		
//		MoneyPaidRecd value = new MoneyPaidRecd();
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement("Select * from partyMoney where id=?")) {
//			ps.setString(1, id + "");
//			ResultSet rs = ps.executeQuery();
//			if (rs.next()) {
//				value.setDate(rs.getString("date"));
//				value.setId(rs.getLong("id"));
//				value.setPaid(rs.getString("paid"));
//				value.setReceived(rs.getString("received"));
//				value.setTitle(rs.getString("title"));
//				value.setPartyType(rs.getString("partyType"));
//				value.setDescription(rs.getString("description"));
//				value.setPaymentMode(rs.getString("paymentMode"));
//				value.setBankName(rs.getString("bankName"));
//				value.setTag(rs.getString("tag"));
//				value.setChequeNo(rs.getString("chequeNo"));
//				value.setDepositDate(rs.getString("depositDate"));
//				Blob blob = rs.getBlob("receipt");
//				if (blob != null) {
//					value.setReceipt(blob.getBinaryStream());
//				}
//			} else {
//				throw new NoSuchElementException("No selected Id in partyMoney table");
//			}
//			ps.close();
//			connection.close();
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		}
//		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IMoneyPaidRecordDao#deleteMoneyPaidRecd(int)
	 */
	@Override
	public boolean deleteMoneyPaidRecd(Long id) {
		
		
		initTemplate();
    	try {
    		int status = template.update("DELETE FROM partyMoney WHERE id=?", id);
    		auditDao.insertAuditRecord(
					new AuditLog(0l, userDao.getCurrentUser(), null, "DELETED paid/received entry:" + id, null, 0l));

    		if(status != 0){
                return true;
            }
		} catch (RuntimeException runtimeException) {

			System.err.println(runtimeException);
			throw runtimeException;
		}
    	
    	return false;
    	
		
//		String sql = "DELETE FROM partyMoney WHERE id=?";
//		try (Connection connection = dataSource.getConnection();
//				final PreparedStatement ps = connection.prepareStatement(sql)) {
//			ps.setLong(1, id);
//			ps.executeUpdate();
//			auditDao.insertAuditRecord(
//					new AuditLog(0l, userDao.getCurrentUser(), null, "DELETED paid/received entry:" + id, null, 0l));
//			ps.close();
//			connection.close();
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return false;
	}

	public DataSource getDataSource() {
		return dataSource;
	}


	private static final String INSERT_PARTY_MONEY_QRY = "INSERT INTO partyMoney ("
			+ "title , partyType , date , paymentMode , paid , received, bankName , chequeNo , depositDate , isAdvanced , receipt, description, tag ) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IMoneyPaidRecordDao#addMoneyPaidRecdInfo(com.
	 * quickveggies.entities.MoneyPaidRecd, java.lang.String)
	 */
	@Override
	public Long addMoneyPaidRecdInfo(MoneyPaidRecd item, String auditLogMsg) {
		
		initInsert();
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("title", item.getTitle());
		args.put("partyType", item.getPartyType());
		args.put("date",item.getDate());
		args.put("paymentMode", item.getPaymentMode());
		args.put("paid", item.getPaid());
		args.put("received",item.getReceived());
		args.put("bankName", item.getBankName());
		args.put("chequeNo", item.getChequeNo());
		args.put("depositDate",item.getDepositDate());
		args.put("isAdvanced", item.getIsAdvanced());
		args.put("receipt", item.getReceipt());
		args.put("description",item.getDescription());
		args.put("tag",item.getTag());
		
		String auditMsg;
		if (auditLogMsg != null && !auditLogMsg.isEmpty()) {
			auditMsg = auditLogMsg;
		} else if ("0".equals(item.getPaid())) {
			auditMsg = "MONEY received from " + item.getTitle();
		} else {
			auditMsg = "MONEY paid to " + item.getTitle();
		}
		
		Long id = insert.executeAndReturnKey(args).longValue();
		auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null, auditMsg, "partyMoney", id));
		return id;
		
		
//		String sql = INSERT_PARTY_MONEY_QRY;
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//			ps.setString(1, mpr.getTitle());
//			ps.setString(2, mpr.getPartyType());
//			ps.setString(3, mpr.getDate());
//			ps.setString(4, mpr.getPaymentMode());
//			ps.setString(5, mpr.getPaid());
//			ps.setString(6, mpr.getReceived());
//			ps.setString(7, mpr.getBankName());
//			ps.setString(8, mpr.getChequeNo());
//			ps.setString(9, mpr.getDepositDate());
//			ps.setString(10, mpr.getIsAdvanced());
//			ps.setBlob(11, mpr.getReceipt());
//			ps.setString(12, mpr.getDescription());
//			ps.setString(13, mpr.getTag());
//
//			ps.executeUpdate();
//			String auditMsg;
//			if (auditLogMsg != null && !auditLogMsg.isEmpty()) {
//				auditMsg = auditLogMsg;
//			} else if ("0".equals(item.getPaid())) {
//				auditMsg = "MONEY received from " + item.getTitle();
//			} else {
//				auditMsg = "MONEY paid to " + item.getTitle();
//			}
//			Long key = (long) databaseClient.getGeneratedKey(ps);
//			auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null, auditMsg, "partyMoney", key));
//			ps.close();
//			connection.close();
//			return key;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return null;
	}

}
