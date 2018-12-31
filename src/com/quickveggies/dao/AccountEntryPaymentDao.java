
package com.quickveggies.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AccountEntryPayment;
import com.quickveggies.impl.IAccountEntryPayementDao;


@Component
public class AccountEntryPaymentDao implements IAccountEntryPayementDao {
	
	
	
	
	@Autowired
	private DataSource dataSource;
	
	 public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public AccountEntryPaymentDao() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IAccountEntryPayementDao#getAccountEntryPayment(int)
	 */
	@Override
	public AccountEntryPayment getAccountEntryPayment(int id) {
	        String sql = "SELECT * FROM accountEntryPayments WHERE id=?";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return new AccountEntryPayment(rs.getInt("id"), rs.getInt("account_entry_id"),
	                        rs.getString("payment_table"), rs.getInt("payment_id"));
	            }
	        }
	        catch (Exception x) {
	            x.printStackTrace();
	        }
	        return new AccountEntryPayment();
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#getAccountEntryPayment(java.lang.String, int)
		 */
	    @Override
		public AccountEntryPayment getAccountEntryPayment(String paymentTable, int paymentId) {
	        String sql = "SELECT * FROM accountEntryPayments WHERE payment_table=? AND payment_id=?";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setString(1, paymentTable);
	            ps.setInt(2, paymentId);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return new AccountEntryPayment(rs.getInt("id"), rs.getInt("account_entry_id"),
	                        rs.getString("payment_table"), rs.getInt("payment_id"));
	            }
	        }
	        catch (Exception x) {
	            x.printStackTrace();
	        }
	        return new AccountEntryPayment();
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#getAccountEntryPayments(int)
		 */
	    @Override
		public List<AccountEntryPayment> getAccountEntryPayments(int accountEntryId) {
	        List<AccountEntryPayment> result = new ArrayList<>();
	        String sql = "SELECT * FROM accountEntryPayments WHERE account_entry_id=?";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, accountEntryId);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                result.add(new AccountEntryPayment(rs.getInt("id"), rs.getInt("account_entry_id"),
	                        rs.getString("payment_table"), rs.getInt("payment_id")));
	            }
	        }
	        catch (Exception x) {
	            x.printStackTrace();
	        }
	        return result;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#addAccountEntryPayment(com.quickveggies.entities.AccountEntryPayment)
		 */
	    @Override
		public Integer addAccountEntryPayment(AccountEntryPayment payment) {
	        String sql = "INSERT INTO accountEntryPayments (account_entry_id, payment_table, payment_id)  VALUES (?, ?, ?)";
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            ps.setInt(1, payment.getAccountEntryId());
//	            ps.setInt(1, 0);
	            ps.setString(2, payment.getPaymentTable());
	            ps.setInt(3, payment.getPaymentId());
	            ps.executeUpdate();
	            return getGeneratedKey(ps);
	        }
	        catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        return null;
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
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#deleteAccountPayment(int)
		 */
	    @Override
		public boolean deleteAccountPayment(int id) {
	        String sql = "DELETE FROM accountEntryPayments WHERE id=?";
	        try (final PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, id);
	            ps.executeUpdate();
	            return true;
	        }
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#deleteAccountPaymentByEntryId(int)
		 */
	    @Override
		public boolean deleteAccountPaymentByEntryId(int accountEntryId) {
	        String sql = "DELETE FROM accountEntryPayments WHERE account_entry_id=?";
	        try (final PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, accountEntryId);
	            ps.executeUpdate();
	            return true;
	        }
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#deleteAccountEntryPaymentByEntryId(int)
		 */
	    @Override
		public boolean deleteAccountEntryPaymentByEntryId(int entryId) {
	        String sql = "DELETE FROM accountEntryPayments WHERE account_entry_id=?";
	        try (final PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, entryId);
	            ps.executeUpdate();
	            return true;
	        }
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    

		
	   

}
