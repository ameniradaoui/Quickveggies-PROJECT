package com.quickveggies.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import com.quickveggies.entities.User;

public interface IUserUtils {

	String getCurrentUser();

	User getUserById(Long id) throws SQLException, NoSuchElementException;

	

	//## changed by ss on 21-Dec-2017
	User getUserByName(String name) throws SQLException, NoSuchElementException;

	User getUserForApproval() throws SQLException;
	//## changed by ss on 21-Dec-2017

	Long saveUser(User user) throws SQLException;

	List<User> geUser();

	

	boolean deleteUser(String name);

	boolean deleteUser(Long id);

}