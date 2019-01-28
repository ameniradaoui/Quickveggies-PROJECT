package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.ExpenseInfo;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.LadaanBijakSaleDeal;
import com.quickveggies.impl.ILadaanBijakDao;

@Component
public class LadaanBijakDao implements ILadaanBijakDao {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private UserUtils userDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ILadaanBijakDao#getLadBijSaleDeal(int)
	 */

	private static RowMapper<LadaanBijakSaleDeal> mapper = new RowMapper<LadaanBijakSaleDeal>() {
		@Override
		public LadaanBijakSaleDeal mapRow(ResultSet rs, int index) throws SQLException {
			LadaanBijakSaleDeal deal = new LadaanBijakSaleDeal();
			deal.setAggregatedAmount(rs.getString("aggregatedAmount"));
			deal.setAmountedTotal(rs.getString("totalAmount"));
			// c.setAmountReceived(rs.getString("industryType"));
			deal.setBuyerRate(rs.getString("buyerRate"));
			deal.setCases(rs.getString("boxes"));
			deal.setDealId(String.valueOf(rs.getInt("dealID")));
			deal.setSaleNo(rs.getInt("id"));
			deal.setFreight(rs.getString("freight"));
			deal.setComission(rs.getString("comission"));
			deal.setDate(rs.getString("dealDate"));

			return deal;
		}
	};
	
	private  RowMapper<Buyer> buyers1 = new RowMapper<Buyer>() {

		@Override
		public Buyer mapRow(ResultSet rs, int rowNum) throws SQLException {
			

			Long id = rs.getLong("id");
			String title = rs.getString("title");
			String firstName = rs.getString("firstName");
			String lastName = rs.getString("lastName");
			String company = rs.getString("company");
			String proprietor = rs.getString("proprietor");
			String mobile = rs.getString("mobile");
			String mobile2 = rs.getString("mobile2");
			String email = rs.getString("email");
			String shopno = rs.getString("shopno");
			String city = rs.getString("city");
			String email2 = rs.getString("email2");
			String parentCompany = rs.getString("parentCompany");
			String paymentMethod = rs.getString("paymentMethod");
			String creditPeriod = rs.getString("creditPeriod");
			String buyerType = rs.getString("buyerType");
			return new Buyer(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
					shopno, city, email2, parentCompany, getPayementMethod(paymentMethod), creditPeriod, buyerType);}
	};

	
	
	private int getPayementMethod(String paymentMethod) {
		try {
			return Integer.parseInt(paymentMethod);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		}
	}
	private static RowMapper<DBuyerTableLine> buyerDeals = new RowMapper<DBuyerTableLine>() {

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

	private JdbcTemplate template;

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);

		}
	}

	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);

	}

//	private ResultSet getResult(String query) throws SQLException {
//		Statement statement = dataSource.getConnection().createStatement();
//		ResultSet resultSet = statement.executeQuery(query);
//
//		return resultSet;
//	}

	@Override
	public LadaanBijakSaleDeal getLadBijSaleDeal(Long dealId) {
		initTemplate();
		
		  List<LadaanBijakSaleDeal> list = template.query("select * from ladaanBijakSaleDeals where dealId=?", mapper , dealId);
		  if (list.isEmpty()){
			  return null;
		  }else {
			  return list.get(0);
		  }
		
		
//		String sql = "select * from ladaanBijakSaleDeals where dealId=?;";
//		LadaanBijakSaleDeal deal = null;
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(sql)) {
//			ps.setLong(1, dealId);
//			ResultSet rs = ps.executeQuery();
//			if (rs.next()) {
//				deal = prepareLadBijObjFromRs(rs);
//			}
//			ps.close();
//			connection.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		return deal;
	}

	public LadaanBijakDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.ILadaanBijakDao#getLadBijSaleDealsForBuyer(java.lang
	 * .String)
	 */
	@Override
	public List<LadaanBijakSaleDeal> getLadBijSaleDealsForBuyer(String buyerTitle) {
		initTemplate();
		
		return template.query("select * from  ladaanBijakSaleDeals where dealID in (select dealID from buyerDeals where buyerTitle = ?)", mapper);
//		String sql = "select * from  ladaanBijakSaleDeals where dealID in (select dealID from buyerDeals where buyerTitle = ?);";
//		List<LadaanBijakSaleDeal> list = new ArrayList<>();
//		LadaanBijakSaleDeal deal = null;
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(sql)) {
//			ps.setString(1, buyerTitle);
//			ResultSet rs = ps.executeQuery();
//			while (rs.next()) {
//				deal = prepareLadBijObjFromRs(rs);
//				list.add(deal);
//			}
//			ps.close();
//			connection.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return list;
	}

//	private LadaanBijakSaleDeal prepareLadBijObjFromRs(ResultSet rs) throws SQLException {
//		LadaanBijakSaleDeal deal = new LadaanBijakSaleDeal();
//		deal.setAggregatedAmount(rs.getString("aggregatedAmount"));
//		deal.setAmountedTotal(rs.getString("totalAmount"));
//		// c.setAmountReceived(rs.getString("industryType"));
//		deal.setBuyerRate(rs.getString("buyerRate"));
//		deal.setCases(rs.getString("boxes"));
//		deal.setDealId(String.valueOf(rs.getInt("dealID")));
//		deal.setSaleNo(rs.getInt("id"));
//		deal.setFreight(rs.getString("freight"));
//		deal.setComission(rs.getString("comission"));
//		deal.setDate(rs.getString("dealDate"));
//		return deal;
//
//	}

	public DataSource getDataSource() {
		return dataSource;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ILadaanBijakDao#getNonEditedLadaanEntries()
	 */
	@Override
	public Long getNonEditedLadaanEntries() {
		initTemplate();
		String sql = "select dealID from buyerDeals where buyerTitle in (select title from buyers1 where RTRIM(LTRIM(LOWER(buyerType))) IN ('ladaan', 'bijak'))";

		List<Long> list = template.query(sql,SingleColumnRowMapper.newInstance(Long.class));
		
		Long count = 0l;
//		try {
//			ResultSet rs = getResult(QRY_DEALS_FOR_LADAAN_BUYER);
//			List<Integer> buyerDealIds = template.query(QRY_DEALS_FOR_LADAAN_BUYER, rse)
//			while (rs.next()) {
//				buyerDealIds.add(rs.getInt(1));
//			}
			if (list.isEmpty()) {
				return 0l;
			}
			String ladanSql = "select count(*) from  ladaanBijakSaleDeals where dealID in (";
			for (int idx = 0; idx < list.size(); idx++) {
				Long dealId = list.get(idx);
				ladanSql = ladanSql.concat(" " + dealId);
				if (idx < list.size() - 1) {
					ladanSql = ladanSql.concat(",");
				}
			}
			ladanSql = ladanSql.concat(");");
			// System.out.println(ladanSql);
			Long ladaanCount=template.query(ladanSql, SingleColumnRowMapper.newInstance(Long.class)).get(0);
			//ResultSet ladanRs = getResult(ladanSql);
			//int ladaanCount = 0;
			//if (ladanRs.next()) {
			//	ladaanCount = ladanRs.getInt(1);
			//}
			count = list.size() - ladaanCount;
			//rs.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			
//		}

		return count;
	}

}
