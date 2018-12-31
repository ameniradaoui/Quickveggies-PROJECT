package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.controller.SessionDataController;
import com.quickveggies.entities.User;
import com.quickveggies.impl.IUserUtils;


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
	public User getUserById(int id) throws SQLException, NoSuchElementException {
	        ResultSet set = getResult("select * from users where id='" + id + "';");
	        if (set.next()) {
	            return getUserByName(set.getString("name"));
	        } else {
	            throw new NoSuchElementException("No such user in database");
	        }
	    }

	  /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getResult(java.lang.String)
	 */
	@Override
	public ResultSet getResult(String query) throws SQLException {
	        Statement statement = dataSource.getConnection().createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        return resultSet;
	    }
	 //## changed by ss on 21-Dec-2017
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getUserByName(java.lang.String)
	 */
    @Override
	public User getUserByName(String name) throws SQLException, NoSuchElementException {
        ResultSet user = getResult("select * from users where name='" + name + "';");
        if (user.next()) {
            int id = user.getInt("id");
            int role = user.getInt("role");
            String password = user.getString("pass");
            String email = user.getString("email");
            boolean usrStatus = user.getBoolean("bool_activestatus");
            String usrType = user.getString("usertype");
            User receivedUser = new User(name, password, email, role,usrStatus,usrType);
            return receivedUser;
        } else {
            throw new NoSuchElementException("No such user in database");
        }
    }

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#getUserForApproval()
	 */
    @Override
	public User getUserForApproval() throws SQLException
    {
    	 ResultSet user = getResult("select * from users where bool_activestatus='"+false+"';");
    	 if (user.next()) 
    	 {
             int id = user.getInt("id");
             String name = user.getString("name");
             String password = user.getString("pass");
             String email = user.getString("email");
             User receivedUser = new User(id, name, password, email);
             return receivedUser;
         } 
    	 else 
    	 {
             throw new NoSuchElementException("No User left for approval!");
         }
    }
    //## changed by ss on 21-Dec-2017
  
    public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IUserUtils#saveUser(com.quickveggies.entities.User)
	 */
    @Override
	public void saveUser(User user) throws SQLException {
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        int role = user.getRole();
        String userType = user.getUsertype();
 
        PreparedStatement prepStmnt = dataSource.getConnection()
                                      .prepareStatement("insert into users (name,pass,email,role,usertype,bool_activestatus) " + "values(?,?,?,?,?,?)");
        prepStmnt.setString(1, name);
        prepStmnt.setString(2, password);
        prepStmnt.setString(3, email);
        prepStmnt.setInt(4, role);
        prepStmnt.setString(5, userType);
        prepStmnt.setBoolean(6, true);
        
        prepStmnt.executeUpdate();
        
        System.out.println("Database: user " + name + " successfully created.");
    }
    
   
	

	
   

    	
    
    
    

}
