
package com.quickveggies.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.Account;
import com.quickveggies.entities.AccountEntryPayment;
import com.quickveggies.impl.IAccountEntryPayementDao;


@Component
public class AccountEntryPaymentDao implements IAccountEntryPayementDao {
	
	private SimpleJdbcInsert insert;
	private JdbcTemplate template;
	
	@Autowired
	private DataSource dataSource;
	
	
	private void initInsert() {
		if (insert == null) {
			createInsert();
			
		}
	}
	
	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("accountEntryPayments").usingGeneratedKeyColumns("id");
	}

	
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();
		
	}
	
	
	 public DataSource getDataSource() {
		return dataSource;
	}

	

	public AccountEntryPaymentDao() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IAccountEntryPayementDao#getAccountEntryPayment(int)
	 */
	
	
	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);
			
		}
	}
	
	private static RowMapper<AccountEntryPayment> mapper = new RowMapper<AccountEntryPayment>() {
		@Override
		public AccountEntryPayment mapRow(ResultSet data, int index) throws SQLException {
			AccountEntryPayment item = new AccountEntryPayment();
			item.setId(data.getLong("id"));
			item.setAccountEntryId(data.getLong("account_entry_id"));
			item.setPaymentTable(data.getString("payment_table"));
			item.setPaymentId(data.getLong("payment_id"));
			
			return item;
		}
	};
	
	
	
	
	@Override
	public AccountEntryPayment getAccountEntryPayment(Long id) {
		
		initTemplate();
	     List<AccountEntryPayment> list= template.query("SELECT * FROM accountEntryPayments WHERE id=?", mapper, id);
	     
	      if (list.isEmpty()){
	    	  return null ;
	      }else{ 
	    	  return list.get(0);
	      }		
	       
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#getAccountEntryPayment(java.lang.String, int)
		 */
	    @Override
		public AccountEntryPayment getAccountEntryPayment(String paymentTable, Long paymentId) {
	    	
	    	initTemplate();
		     List<AccountEntryPayment> list= template.query("SELECT * FROM accountEntryPayments WHERE payment_table=? AND payment_id=?", mapper, paymentTable,paymentId);
		     
		      if (list.isEmpty()){
		    	  return null ;
		      }else{ 
		    	  return list.get(0);
		      }	
	    	
	    	
//	        String sql = "SELECT * FROM accountEntryPayments WHERE payment_table=? AND payment_id=?";
//	        try ( Connection connection = dataSource.getConnection();
//	        		PreparedStatement ps = connection.prepareStatement(sql)) {
//	            ps.setString(1, paymentTable);
//	            ps.setLong(2, paymentId);
//	            ResultSet rs = ps.executeQuery();
//	            if (rs.next()) {
//	            	ps.close();
//	            	 connection.close();
//	                return new AccountEntryPayment(rs.getLong("id"), rs.getLong("account_entry_id"),
//	                        rs.getString("payment_table"), rs.getLong("payment_id"));
//	            }
//	        }
//	        catch (Exception x) {
//	            x.printStackTrace();
//	        }
//	      
//	        return new AccountEntryPayment();
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#getAccountEntryPayments(int)
		 */
	    @Override
		public List<AccountEntryPayment> getAccountEntryPayments(Long accountEntryId) {
	    	
	    	initTemplate();
		     List<AccountEntryPayment> list= template.query("SELECT * FROM accountEntryPayments WHERE account_entry_id=?", mapper, accountEntryId);
		     
		      if (list.isEmpty()){
		    	  return null ;
		      }else{ 
		    	  return list;
		      }	
	    	
//	    	
//	        List<AccountEntryPayment> result = new ArrayList<>();
//	        String sql = "SELECT * FROM accountEntryPayments WHERE account_entry_id=?";
//	        try ( Connection connection = dataSource.getConnection();
//	        		PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
//	            ps.setLong(1, accountEntryId);
//	            ResultSet rs = ps.executeQuery();
//	            while (rs.next()) {
//	            	ps.close();
//	            	  connection.close();
//	                result.add(new AccountEntryPayment(rs.getLong("id"), rs.getLong("account_entry_id"),
//	                        rs.getString("payment_table"), rs.getLong("payment_id")));
//	            }
//	        }
//	        catch (Exception x) {
//	            x.printStackTrace();
//	        }
//	        return result;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#addAccountEntryPayment(com.quickveggies.entities.AccountEntryPayment)
		 */
	    @Override
		public Long addAccountEntryPayment(AccountEntryPayment item) {
	    	
	    	
	    	initInsert();
	    	
			Map<String, Object> args = new HashMap<String, Object>();
			// args.put("id", item.getId());
			args.put(" account_entry_id", item.getAccountEntryId());
			args.put("payment_table", item.getPaymentTable());
			args.put("payment_id", item.getPaymentId());
			
			
			
			Long id =  insert.executeAndReturnKey(args).longValue();
	    	
			return id;
	    	
//	        String sql = "INSERT INTO accountEntryPayments (account_entry_id, payment_table, payment_id)  VALUES (?, ?, ?)";
//	        try ( Connection connection = dataSource.getConnection();
//	        		PreparedStatement ps = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//	            ps.setLong(1, payment.getAccountEntryId());
////	            ps.setInt(1, 0);
//	            ps.setString(2, payment.getPaymentTable());
//	            ps.setLong(3, payment.getPaymentId());
//	            ps.executeUpdate();
//	            ps.close();
//	            connection.close();
//	            return getGeneratedKey(ps);
//	        }
//	        catch (SQLException ex) {
//	            ex.printStackTrace();
//	        }
//	        return null;
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
		public boolean deleteAccountPayment(Long id) {
	    	initTemplate();
	    	try {
	    		int status = template.update("DELETE FROM accountEntryPayments WHERE id=?", id);
	    		
	    		if(status != 0){
	                return true;
	            }
			} catch (RuntimeException runtimeException) {

				System.err.println(runtimeException);
				throw runtimeException;
			}
	    	
	    	return false;
	    	
	    	
//	        String sql = "DELETE FROM accountEntryPayments WHERE id=?";
//	        try (final PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
//	            ps.setLong(1, id);
//	            ps.executeUpdate();
//	            ps.close();
//	            return true;
//	        }
//	        catch (SQLException e) {
//	            e.printStackTrace();
//	        }
//	        return false;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#deleteAccountPaymentByEntryId(int)
		 */
	    @Override
		public boolean deleteAccountPaymentByEntryId(Long accountEntryId) {
	    	
	    	try {
	    		int status = template.update("DELETE FROM accountEntryPayments WHERE account_entry_id=?", accountEntryId);
	    		
	    		if(status != 0){
	                return true;
	            }
			} catch (RuntimeException runtimeException) {

				System.err.println(runtimeException);
				throw runtimeException;
			}
	    	
	    	return false;
	    	  	
	    	
//	        String sql = "DELETE FROM accountEntryPayments WHERE account_entry_id=?";
//	        try ( Connection connection = dataSource.getConnection();
//	        		final PreparedStatement ps = connection.prepareStatement(sql)) {
//	            ps.setLong(1, accountEntryId);
//	            ps.executeUpdate();
//	            ps.close();
//	            connection.close();
//	            return true;
//	        }
//	        catch (SQLException e) {
//	            e.printStackTrace();
//	        }
//	        return false;
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IAccountEntryPayementDao#deleteAccountEntryPaymentByEntryId(int)
		 */
	    @Override
		public boolean deleteAccountEntryPaymentByEntryId(Long entryId) {
	    	

	    	try {
	    		int status = template.update("DELETE FROM accountEntryPayments WHERE account_entry_id=?", entryId);
	    		
	    		if(status != 0){
	                return true;
	            }
			} catch (RuntimeException runtimeException) {

				System.err.println(runtimeException);
				throw runtimeException;
			}
	    	
	    	return false;
	    	
//	    	
//	        String sql = "DELETE FROM accountEntryPayments WHERE account_entry_id=?";
//	        try ( Connection connection = dataSource.getConnection();
//	        		final PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
//	            ps.setLong(1, entryId);
//	            ps.executeUpdate();
//	            ps.close();
//	            connection.close();
//	            return true;
//	        }
//	        catch (SQLException e) {
//	            e.printStackTrace();
//	        }
//	        return false;
	    }
	    

		
	   

}
