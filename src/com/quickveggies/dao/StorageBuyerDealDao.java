package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.ExpenseInfo;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.StorageBuyerDeal;
import com.quickveggies.impl.IStorageBuyerDealDao;

@Component
public class StorageBuyerDealDao implements IStorageBuyerDealDao {

	@Autowired
	private DataSource dataSource;

	public StorageBuyerDealDao() {
		super();

	}

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
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();

	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("storageBuyerDeals").usingGeneratedKeyColumns("id");
	}

	private static RowMapper<DBuyerTableLine> mapperBuyer = new RowMapper<DBuyerTableLine>() {

		@Override
		public DBuyerTableLine mapRow(ResultSet set, int rowNum) throws SQLException {

			String title, date, sum, boxes, rate, amountReceived, dealID, aggregatedAmt, fruit, qualityType,
					boxSizeType;

			title = set.getString("buyerTitle");
			date = set.getString("dealDate");
			rate = set.getString("buyerRate");
			sum = set.getString("buyerPay");
			boxes = set.getString("boxes");
			amountReceived = set.getString("amountReceived");
			dealID = set.getInt("dealID") + "";
			aggregatedAmt = set.getString("aggregatedAmount");
			qualityType = set.getString("qualityType");
			boxSizeType = set.getString("boxSizeType");
			fruit = set.getString("fruit");
			return new DBuyerTableLine(title, date, rate, sum, boxes, "" + set.getInt("id"), amountReceived,
					aggregatedAmt, dealID, fruit, qualityType, boxSizeType);
		}
	};
	private static RowMapper<StorageBuyerDeal> mapperStorage = new RowMapper<StorageBuyerDeal>() {
		@Override
		public StorageBuyerDeal mapRow(ResultSet data, int index) throws SQLException {
			StorageBuyerDeal item = new StorageBuyerDeal();
			item.setBuyerDealLineId(data.getLong("buyerDealLineId"));
			item.setStrorageDealLineId(data.getLong("strorageDealLineId"));

			return item;
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IStorageBuyerDealDao#addStorageBuyerDealInfo(java.
	 * lang.Integer, java.lang.Integer)
	 */
	private void createstorage(StorageBuyerDeal item){
		
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("newBuyerDealId", item.getBuyerDealLineId());
		args.put("strorageDealLineId", item.getStrorageDealLineId());
		
	}

	
	@Override
	public void addStorageBuyerDealInfo(Long newBuyerDealId, Long strorageDealLineId) {
		
		StorageBuyerDeal storage=new StorageBuyerDeal();
		storage.setBuyerDealLineId(newBuyerDealId);
		storage.setStrorageDealLineId(strorageDealLineId);
		createstorage(storage);
	
		
		
		// try ( Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement(
		// "INSERT INTO storageBuyerDeals (" + "buyerDealLineId,
		// strorageDealLineId) VALUES (?, ?)")) {
		// ps.setLong(1, newBuyerDealId);
		// ps.setLong(2, strorageDealLineId);
		// ps.executeUpdate();
		// ps.close();
		// connection.close();
		//
		// } catch (SQLException ex) {
		// ex.printStackTrace();
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IStorageBuyerDealDao#getStorageBuyerDeals()
	 */
	@Override
	public List<StorageBuyerDeal> getStorageBuyerDeals() {
		initTemplate();
		return template.query("Select * from storageBuyerDeals;", mapperStorage);
		// List<StorageBuyerDeal> list = new ArrayList<>();
		// try ( Connection connection = dataSource.getConnection();
		// PreparedStatement ps =
		// dataSource.getConnection().prepareStatement("Select * from
		// storageBuyerDeals;")) {
		// ResultSet rs = ps.executeQuery();
		// while (rs.next()) {
		// StorageBuyerDeal deal = new StorageBuyerDeal(rs.getInt(1),
		// rs.getInt(2));
		// list.add(deal);
		//
		// }
		// ps.close();
		// connection.close();
		// } catch (SQLException x) {
		// x.printStackTrace();
		// }
		// return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IStorageBuyerDealDao#getStorageBuyerDeal(java.lang.
	 * Integer)
	 */
	@Override
	public StorageBuyerDeal getStorageBuyerDeal(Long id) throws SQLException {
		initTemplate();
		return template.query("Select * from storageBuyerDeals;", mapperStorage).get(0);
		// Connection connection = dataSource.getConnection();
		// PreparedStatement ps = connection.prepareStatement("Select * from
		// storageBuyerDeals;");
		// ResultSet rs = ps.executeQuery();
		// if (rs.next()) {
		// StorageBuyerDeal deal = new StorageBuyerDeal(rs.getInt(1),
		// rs.getInt(2));
		// ps.close();
		// connection.close();
		// return deal;
		//
		// }
		// throw new RuntimeException("no such element");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IStorageBuyerDealDao#getStorageDealsCount(java.lang.
	 * String)
	 */
	@Override
	public int getStorageDealsCount(String storeType) {
		initTemplate();
		
		String sql = "select sum(CAST (boxes AS bigint)) from buyerDeals group by buyerTitle having buyerTitle=?";
		List<Integer> results = template.query(sql, SingleColumnRowMapper.newInstance(Integer.class), storeType);
       if (results.isEmpty()){
    	   return 0 ;
       } else {
    	   return results.get(0);
       }
		
		
		
//		String sql = "select sum(CAST (boxes AS bigint)) from buyerDeals group by buyerTitle having buyerTitle=?;";
//		
//		int result = 0;
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(sql)) {
//			ps.setString(1, storeType);
//			ResultSet rs = ps.executeQuery();
//			if (rs.next()) {
//
//				result = rs.getInt(1);
//				rs.close();
//			}
//			ps.close();
//			connection.close();
//		} catch (Exception x) {
//			x.printStackTrace();
//		}
		
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
