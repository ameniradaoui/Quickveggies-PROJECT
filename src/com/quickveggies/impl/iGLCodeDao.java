package com.quickveggies.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.quickveggies.entities.GLCode;

public interface iGLCodeDao {

	ResultSet getResult(String query) throws SQLException;

	List<GLCode> getGLCode();

	

	void saveGLCode() throws SQLException;

}