package com.quickveggies.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.quickveggies.entities.User;

public interface IUserUtils {

	String getCurrentUser();

	User getUserById(int id) throws SQLException, NoSuchElementException;

	ResultSet getResult(String query) throws SQLException;

	//## changed by ss on 21-Dec-2017
	User getUserByName(String name) throws SQLException, NoSuchElementException;

	User getUserForApproval() throws SQLException;
	//## changed by ss on 21-Dec-2017

	void saveUser(User user) throws SQLException;

}