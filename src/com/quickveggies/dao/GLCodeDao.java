package com.quickveggies.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.log.Logger;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.User;
import com.quickveggies.impl.iGLCodeDao;

@Component
public class GLCodeDao implements iGLCodeDao {

	
	
	@Autowired
	 private DataSource dataSource;
	
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public ResultSet getResult(String query) throws SQLException {
	        Statement statement = dataSource.getConnection().createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        return resultSet;
	    }
	
	 @Override
		public List<GLCode> getGLCode() {
			List<GLCode> list = new ArrayList<>();
			try ( Connection connection = dataSource.getConnection();
					PreparedStatement ps = dataSource.getConnection().prepareStatement("select * from GLCode;")) {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					GLCode deal = new GLCode( rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));

					list.add(deal);
					
					
					
					
				}
			} catch (SQLException x) {
				x.printStackTrace();
			}
			return list;
		}
		
	  @Override
		public void saveGLCode() throws SQLException {
	       
		  try {
			  Connection connection = dataSource.getConnection();
		        PreparedStatement prepStmnt = connection
		                                      .prepareStatement("insert into GLCode (AccountCode,NameOfLedger,GLCode,AccountType, Description) " + "values(?,?,?,?,?)");
		        
			FileInputStream fileIn = new FileInputStream(new File("Chart of Accounts GL CODES.xlsx"));
		     XSSFWorkbook wb = new XSSFWorkbook(fileIn);
		     XSSFSheet sheet = wb.getSheetAt(0);
		     Row row;
		     
		     for (int i =1 ; i<=sheet.getLastRowNum();i++){
		    	 row = sheet.getRow(i);
		    	 prepStmnt.setString(1, row.getCell(0).getStringCellValue());
		    	 prepStmnt.setString(2, row.getCell(1).getStringCellValue());
		    	 prepStmnt.setString(3, row.getCell(2).getStringCellValue());
		    	 prepStmnt.setString(4, row.getCell(3).getStringCellValue());
		    	 prepStmnt.setString(5, row.getCell(4).getStringCellValue());
		    	
		         prepStmnt.execute();
		    	 
		     }
	       
	       } catch (SQLException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	        
	    }
	 
	 
}
