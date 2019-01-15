package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.StorageBuyerDeal;
import com.quickveggies.impl.IStorageBuyerDealDao;


@Component
public class StorageBuyerDealDao implements IStorageBuyerDealDao {

	
	@Autowired
	private DataSource dataSource;
	
	

	public StorageBuyerDealDao() {
		super();
		
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IStorageBuyerDealDao#addStorageBuyerDealInfo(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void addStorageBuyerDealInfo(Integer buyerDealLineId, Integer strorageDealLineId) {
		try ( Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO storageBuyerDeals (" + "buyerDealLineId,   strorageDealLineId)  VALUES (?, ?)")) {
			ps.setInt(1, buyerDealLineId);
			ps.setInt(2, strorageDealLineId);
			ps.executeUpdate();
			connection.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IStorageBuyerDealDao#getStorageBuyerDeals()
	 */
	@Override
	public List<StorageBuyerDeal> getStorageBuyerDeals() {
		List<StorageBuyerDeal> list = new ArrayList<>();
		try ( Connection connection = dataSource.getConnection();
				PreparedStatement ps = dataSource.getConnection().prepareStatement("Select * from storageBuyerDeals;")) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				StorageBuyerDeal deal = new StorageBuyerDeal(rs.getInt(1), rs.getInt(2));
				list.add(deal);
				connection.close();
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IStorageBuyerDealDao#getStorageBuyerDeal(java.lang.Integer)
	 */
	@Override
	public StorageBuyerDeal getStorageBuyerDeal(Integer id) throws SQLException {
		 Connection connection = dataSource.getConnection();
		PreparedStatement ps = connection.prepareStatement("Select * from storageBuyerDeals;");
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			StorageBuyerDeal deal = new StorageBuyerDeal(rs.getInt(1), rs.getInt(2));
			connection.close();
			return deal;
			
		}
		throw new RuntimeException("no such element");

	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IStorageBuyerDealDao#getStorageDealsCount(java.lang.String)
	 */
	@Override
	public int getStorageDealsCount(String storeType) {
		String sql = "select sum(CAST (boxes AS bigint)) from buyerDeals group by buyerTitle having buyerTitle=?;";
		int result = 0;
		try ( Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, storeType);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				
				result = rs.getInt(1);
				rs.close();
			}
			connection.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return result;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	


}
