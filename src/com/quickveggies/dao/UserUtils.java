package com.quickveggies.dao;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.controller.SessionDataController;
import com.quickveggies.entities.AccountEntryPayment;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.StorageBuyerDeal;
import com.quickveggies.entities.User;
import com.quickveggies.impl.IUserUtils;

import javafx.collections.ObservableList;


@Component
public class UserUtils implements IUserUtils {
	
	
	@Autowired
	 private DataSource dataSource;
	
	 
	public UserUtils() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getCurrentUser()
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
		insert = new SimpleJdbcInsert(dataSource).withTableName("users").usingGeneratedKeyColumns("id");
	}

	
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();
		
	}

	private static RowMapper<User> mapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet data, int index) throws SQLException {
			User item = new User();
			item.setId(data.getLong("id"));
			item.setName(data.getString("name"));
			item.setPassword(data.getString("pass"));
			item.setRole(data.getInt("role"));
			item.setBool_status(data.getBoolean("bool_activestatus"));
			item.setUsertype(data.getString("usertype"));

			return item;
		}
	};
	
	@Override
	public String getCurrentUser() {
		  SessionDataController session = SessionDataController.getInstance();
	        User user = session.getCurrentUser();
	        String userName = "tzt";
	        if (user != null) 
	        {
	            userName = session.getCurrentUser().getName();
	        }
	        
	        return userName;
	}
	  /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getUserById(int)
	 */
	@Override
	public User getUserById(Long id) throws SQLException, NoSuchElementException {
		
		initTemplate();
	     List<User> list= template.query("select * from users where id=?", mapper, id);
	     
	      if (list.isEmpty()){
	    	  throw new NoSuchElementException("No such user in database");
	      }else{ 
	    	  return list.get(0);
	    	 
	      }		
	       
//		
//	        ResultSet set = getResult("select * from users where id='" + id + "';");
//	        if (set.next()) {
//	        	set.close();
//	            return getUserByName(set.getString("name"));
//	        } else {
//	            throw new NoSuchElementException("No such user in database");
//	        }
	    }

	  /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getResult(java.lang.String)
	 */
//	@Override
//	public ResultSet getResult(String query) throws SQLException {
//	        Statement statement = dataSource.getConnection().createStatement();
//	        ResultSet resultSet = statement.executeQuery(query);
//	        return resultSet;
//	    }
	 //## changed by ss on 21-Dec-2017
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getUserByName(java.lang.String)
	 */
    @Override
	public User getUserByName(String name) throws SQLException, NoSuchElementException {
    	
    	
    	initTemplate();
	     List<User> list= template.query("select * from users where name=?", mapper, name);
	     
	      if (list.isEmpty()){
	    	  return null ;
	      }else{ 
	    	  return list.get(0);
	      }		
	       
//    	
//        ResultSet user = getResult("select * from users where name='" + name + "';");
//        if (user.next()) {
//            int id = user.getInt("id");
//            int role = user.getInt("role");
//            String password = user.getString("pass");
//            String email = user.getString("email");
//            boolean usrStatus = user.getBoolean("bool_activestatus");
//            String usrType = user.getString("usertype");
//            User receivedUser = new User(name, password, email, role,usrStatus,usrType);
//            user.close();
//            return receivedUser;
//        } else {
//            throw new NoSuchElementException("No such user in database");
//        }
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getUserForApproval()
	 */
    @Override
	public User getUserForApproval() throws SQLException
    {

    	initTemplate();
	     List<User> list= template.query("select * from users where bool_activestatus=?", mapper, false);
	     
	      if (list.isEmpty()){
	    	  throw new NoSuchElementException("No User left for approval!");
	      }else{ 
	    	  return list.get(0);
	      }		
    	
//    	
//    	 ResultSet user = getResult("select * from users where bool_activestatus='"+false+"';");
//    	 if (user.next()) 
//    	 {
//             Long id = user.getLong("id");
//             String name = user.getString("name");
//             String password = user.getString("pass");
//             String email = user.getString("email");
//             User receivedUser = new User(id, name, password, email);
//             user.close();
//             return receivedUser;
//         } 
//    	 else 
//    	 {
//             throw new NoSuchElementException("No User left for approval!");
//         }
    }
    //## changed by ss on 21-Dec-2017
  
    public DataSource getDataSource() {
		return dataSource;
	}
	
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#saveUser(com.quickveggies.entities.User)
	 */
    @Override
	public Long saveUser(User item) throws SQLException {
    	
    	initInsert();
    	
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("name", item.getName());
		args.put("pass", item.getPassword());
		args.put("email", item.getEmail());
		args.put("role", item.getRole());
		args.put("usertype", item.getUsertype());
		args.put("bool_activestatus", true);
		
		
		
		
		Long id =  insert.executeAndReturnKey(args).longValue();
    	
		return id;
    	
//        String name = user.getName();
//        String email = user.getEmail();
//        String password = user.getPassword();
//        int role = user.getRole();
//        String userType = user.getUsertype();
//        Connection connection = dataSource.getConnection();
//        PreparedStatement prepStmnt = connection
//                                      .prepareStatement("insert into users (name,pass,email,role,usertype,bool_activestatus) " + "values(?,?,?,?,?,?)");
//        prepStmnt.setString(1, name);
//        prepStmnt.setString(2, password);
//        prepStmnt.setString(3, email);
//        prepStmnt.setInt(4, role);
//        prepStmnt.setString(5, userType);
//        prepStmnt.setBoolean(6, true);
//        
//        prepStmnt.executeUpdate();
//        prepStmnt.close();
//        connection.close();
//        System.out.println("Database: user " + name + " successfully created.");
//		return null;
    }
    
   
    
    @Override
	public List<User> geUser() {
    	
    	
    	initTemplate();
    	return template.query("select * from  users where usertype != 'Admin';", mapper);
    	
//		List<User> list = new ArrayList<>();
//		try ( Connection connection = dataSource.getConnection();
//				PreparedStatement ps = dataSource.getConnection().prepareStatement("select * from  users where usertype != 'Admin';")) {
//			ResultSet rs = ps.executeQuery();
//			while (rs.next()) {
//				User deal = new User(rs.getLong(1), rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getBoolean(6),rs.getString(7));
//
//				list.add(deal);
//				
//				System.out.println(list);
//				
//				
//			}
//			ps.close();
//			connection.close();
//		} catch (SQLException x) {
//			x.printStackTrace();
//		}
//		return list;
	}
	

    @Override
	public boolean deleteUser(Long id) {
    	
    	
    	try {
    		int status = template.update("DELETE FROM users WHERE id=?", id);
    		
    		if(status != 0){
                return true;
            }
		} catch (RuntimeException runtimeException) {

			System.err.println(runtimeException);
			throw runtimeException;
		}
    	
    	return false;
//        String sql = "DELETE FROM users WHERE id=?";
//        try ( Connection connection = dataSource.getConnection();
//        		final PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setLong(1, id);
//            ps.executeUpdate();
//            ps.close();
//             connection.close();
//            return true;
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
        
    }
	@Override
	public boolean deleteUser(String name) {
		// TODO Auto-generated method stub
		return false;
	}



    	
    
    
    

}
