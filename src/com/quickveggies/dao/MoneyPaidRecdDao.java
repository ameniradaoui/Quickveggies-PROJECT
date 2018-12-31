package com.quickveggies.dao;

import java.sql.Blob;
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
	private BuyerDao bd ;
	@Autowired
    private SupplierDao supplierDao;
	@Autowired
    private DatabaseClient databaseClient;
	@Autowired
    private DSalesTableDao dSalesDao ;
	@Autowired
    private AuditDao auditDao ;
	@Autowired
    private UserUtils userDao ; 
	   /* (non-Javadoc)
	 * @see com.quickveggies.dao.IMoneyPaidRecordDao#addMoneyPaidRecdInfo(com.quickveggies.entities.MoneyPaidRecd)
	 */
	@Override
	public Integer addMoneyPaidRecdInfo(MoneyPaidRecd mpr) {
	        return this.addMoneyPaidRecdInfo(mpr, "");
	    }
	   private static final String QRY_PAID_RECD_MONEY_FOR_PARTY_ = "Select * from partyMoney where RTRIM(LTRIM(LOWER(partyType))) = ? ";
	   private static final String QRY_PAID_RECD_MONEY_FOR_PARTY = "Select * from partyMoney where RTRIM(LTRIM(LOWER(partyType))) = ?";

	   private static final String QRY_PAID_RECD_MONEY_FOR_TITLE = " AND RTRIM(LTRIM(LOWER(title))) = ? ";

	   
	   
	   /* (non-Javadoc)
	 * @see com.quickveggies.dao.IMoneyPaidRecordDao#getMoneyPaidRecdList(java.lang.String, com.quickveggies.model.EntityType)
	 */
	@Override
	public List<MoneyPaidRecd> getMoneyPaidRecdList(String partyTitle, EntityType pType) {
	        List<MoneyPaidRecd> list = new ArrayList<>();
	        String query = QRY_PAID_RECD_MONEY_FOR_PARTY
	                + (partyTitle == null ? "" : QRY_PAID_RECD_MONEY_FOR_TITLE) + ";";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(query)) {
	            ps.setString(1, pType.getValue().trim().toLowerCase());
	            if (partyTitle != null) {
	                ps.setString(2, partyTitle.toLowerCase().trim());
	            }
	            ResultSet rs = ps.executeQuery();
	            addMprRsToList(rs, list);
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	        }
	        return list;
	    }

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IMoneyPaidRecordDao#getAllMoneyPaidRecdList(com.quickveggies.model.EntityType)
		 */
	    @Override
		public List<MoneyPaidRecd> getAllMoneyPaidRecdList(EntityType pType) {
	        List<MoneyPaidRecd> list = new ArrayList<>();
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(QRY_PAID_RECD_MONEY_FOR_PARTY)) {
	            ps.setString(1, pType.getValue().trim().toLowerCase());
	            ResultSet rs = ps.executeQuery();
	            addMprRsToList(rs, list);
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	        }
	        return list;
	    }
	    
	    
	    public MoneyPaidRecdDao() {
			super();
			// TODO Auto-generated constructor stub
		}

		private void addMprRsToList(ResultSet rs, List<MoneyPaidRecd> list) throws SQLException {
	        while (rs.next()) {
	            MoneyPaidRecd mpr = new MoneyPaidRecd();
	            mpr.setDate(rs.getString("date"));
	            mpr.setId(rs.getInt("id"));
	            mpr.setPaid(rs.getString("paid"));
	            mpr.setReceived(rs.getString("received"));
	            mpr.setTitle(rs.getString("title"));
	            mpr.setPartyType(rs.getString("partyType"));
	            mpr.setDescription(rs.getString("description"));
	            mpr.setPaymentMode(rs.getString("paymentMode"));
	            mpr.setBankName(rs.getString("bankName"));
	            mpr.setChequeNo(rs.getString("chequeNo"));
	            mpr.setDepositDate(rs.getString("depositDate"));
	            Blob blob = rs.getBlob("receipt");
	            if (blob != null) {
	                mpr.setReceipt(blob.getBinaryStream());
	            }
	            list.add(mpr);
	        }
	    }
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IMoneyPaidRecordDao#getAdvanceMoneyPaidList(com.quickveggies.model.EntityType)
		 */
	    @Override
		public List<MoneyPaidRecd> getAdvanceMoneyPaidList(EntityType pType) {
	        List<MoneyPaidRecd> list = new ArrayList<>();
	        try (PreparedStatement ps = dataSource.getConnection()
	                .prepareStatement("Select * from partyMoney where PartyType=? and isAdvanced='true' ")) {
	            ps.setString(1, pType.getValue().trim().toLowerCase());
	            ResultSet rs = ps.executeQuery();
	            addMprRsToList(rs, list);
	        } catch (SQLException sqle) {
	            sqle.printStackTrace();
	        }
	        return list;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IMoneyPaidRecordDao#getMoneyPaidRecd(int)
		 */
	    @Override
		public MoneyPaidRecd getMoneyPaidRecd(int id) {
	        MoneyPaidRecd value = new MoneyPaidRecd();
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(
	                "Select * from partyMoney where id=?")) {
	            ps.setString(1, id + "");
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                value.setDate(rs.getString("date"));
	                value.setId(rs.getInt("id"));
	                value.setPaid(rs.getString("paid"));
	                value.setReceived(rs.getString("received"));
	                value.setTitle(rs.getString("title"));
	                value.setPartyType(rs.getString("partyType"));
	                value.setDescription(rs.getString("description"));
	                value.setPaymentMode(rs.getString("paymentMode"));
	                value.setBankName(rs.getString("bankName"));
	                value.setChequeNo(rs.getString("chequeNo"));
	                value.setDepositDate(rs.getString("depositDate"));
	                Blob blob = rs.getBlob("receipt");
	                if (blob != null) {
	                    value.setReceipt(blob.getBinaryStream());
	                }
	            }
	            else {
	                throw new NoSuchElementException("No selected Id in partyMoney table");
	            }
	        }
	        catch (SQLException sqle) {
	            sqle.printStackTrace();
	        }
	        return value;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IMoneyPaidRecordDao#deleteMoneyPaidRecd(int)
		 */
	    @Override
		public boolean deleteMoneyPaidRecd(int id) {
	        String sql = "DELETE FROM partyMoney WHERE id=?";
	        try (final PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, id);
	            ps.executeUpdate();
	            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, "DELETED paid/received entry:" + id, null, 0));
	            return true;
	        }
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    public DataSource getDataSource() {
			return dataSource;
		}

		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}
		private static final String INSERT_PARTY_MONEY_QRY = "INSERT INTO partyMoney ("
	            + "title , partyType , date , paymentMode , paid , received, bankName , chequeNo , depositDate , isAdvanced , receipt, description ) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

	   
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IMoneyPaidRecordDao#addMoneyPaidRecdInfo(com.quickveggies.entities.MoneyPaidRecd, java.lang.String)
		 */
	    @Override
		public Integer addMoneyPaidRecdInfo(MoneyPaidRecd mpr, String auditLogMsg) {
	        String sql = INSERT_PARTY_MONEY_QRY;
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            ps.setString(1, mpr.getTitle());
	            ps.setString(2, mpr.getPartyType());
	            ps.setString(3, mpr.getDate());
	            ps.setString(4, mpr.getPaymentMode());
	            ps.setString(5, mpr.getPaid());
	            ps.setString(6, mpr.getReceived());
	            ps.setString(7, mpr.getBankName());
	            ps.setString(8, mpr.getChequeNo());
	            ps.setString(9, mpr.getDepositDate());
	            ps.setString(10, mpr.getIsAdvanced());
	            ps.setBlob(11, mpr.getReceipt());
	            ps.setString(12, mpr.getDescription());
	            ps.executeUpdate();
	            String auditMsg;
	            if (auditLogMsg != null && !auditLogMsg.isEmpty()) {
	                auditMsg = auditLogMsg;
	            }
	            else if ("0".equals(mpr.getPaid())) {
	                auditMsg = "MONEY received from " + mpr.getTitle();
	            }
	            else {
	                auditMsg = "MONEY paid to " + mpr.getTitle();
	            }
	            int key = databaseClient.getGeneratedKey(ps);
	            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null, auditMsg, "partyMoney", key));
	            return key;
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        return null;
	    }

		
}
