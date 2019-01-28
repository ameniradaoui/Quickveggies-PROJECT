package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;

import com.quickveggies.entities.ArrivalSelectionBar;
import com.quickveggies.entities.ArrivalSelectionFilter;
import com.quickveggies.entities.Company;
import com.quickveggies.entities.DSalesTableLine;
import com.quickveggies.impl.IDsalesTableDao;

@Component
public class DSalesTableDao implements IDsalesTableDao {

	@Autowired
	private DataSource dataSource;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntryLineFromSql(int)
	 */

	private static RowMapper<ArrivalSelectionFilter> selectionFilerMapper = new RowMapper<ArrivalSelectionFilter>() {

		@Override
		public ArrivalSelectionFilter mapRow(ResultSet rs, int rowNum) throws SQLException {
			ArrivalSelectionFilter ret = new ArrivalSelectionFilter();
			ret.setYear(rs.getInt(2));
			ret.setFruit(rs.getString(1));
			return ret;
		}

	};
	
	private static RowMapper<ArrivalSelectionBar> selectionFilerMapperBar = new RowMapper<ArrivalSelectionBar>() {

		@Override
		public ArrivalSelectionBar mapRow(ResultSet rs, int rowNum) throws SQLException {
			ArrivalSelectionBar ret = new ArrivalSelectionBar();
			ret.setYear(rs.getInt(1));
			ret.setSommenet(rs.getString(2));
			return ret;
		}

	};

	private static RowMapper<DSalesTableLine> mapper = new RowMapper<DSalesTableLine>() {

		@Override
		public DSalesTableLine mapRow(ResultSet set, int rowNum) throws SQLException {

			String fruit, date, challan, supplier, totalQuantity, fullCase, halfCase, agent, truck, driver, gross,
					charges, net, remarks, dealID, type, amanat;
			fruit = set.getString("fruit");
			date = set.getString("date");
			gross = set.getString("gross");
			agent = set.getString("fwagent");
			challan = set.getString("challan");
			halfCase = set.getString("halfCase");
			fullCase = set.getString("fullCase");
			truck = set.getString("truck");
			driver = set.getString("driver");
			charges = set.getString("charges");
			remarks = set.getString("remarks");
			net = set.getString("net");
			supplier = set.getString("supplier");
			totalQuantity = set.getString("totalQuantity");
			dealID = "" + set.getInt("dealID");
			type = set.getString("type");
			amanat = set.getString("amanat");
			return new DSalesTableLine(fruit, set.getInt("id") + "", date, challan, supplier, totalQuantity, fullCase, halfCase,
					agent, truck, driver, gross, charges, net, remarks, dealID, type, amanat);
		}

	};
	


	private JdbcTemplate template;

	@Override
	public DSalesTableLine getSalesEntryLineFromSql(Long id) throws SQLException, NoSuchElementException {
		initTemplate();
		String sql = "select * from arrival where id = ?";
		List<DSalesTableLine> list = template.query(sql, mapper , id);
		if (list.isEmpty()){
			return null;
		}
		else {
			return list.get(0);
		}
		
//	      final DSalesTableLine dSalesTableLine = new DSalesTableLine();
//	      template.query(sql,new Object[] {id}, 
//	            new RowCallbackHandler() {
//	                 public void processRow(ResultSet rs) throws SQLException {
//	                	
//	                	 dSalesTableLine.setFruit(rs.getString("fruit"));
//	                	 dSalesTableLine.setDate(rs.getString("date"));
//	                	 dSalesTableLine.setGross(rs.getString("gross"));
//	                	 dSalesTableLine.setAgent(rs.getString("fwagent"));
//	                	 dSalesTableLine.setChallan(rs.getString("challan"));
//	                	 dSalesTableLine.setHalfCase(rs.getString("halfCase"));
//	                	 dSalesTableLine.setFullCase(rs.getString("fullCase"));
//	                	 dSalesTableLine.setTruck(rs.getString("truck"));
//	                	 dSalesTableLine.setDriver(rs.getString("driver"));
//	                	 dSalesTableLine.setCharges(rs.getString("charges"));
//	                	 dSalesTableLine.setRemarks(rs.getString("remarks"));
//	                	 dSalesTableLine.setNet(rs.getString("net"));
//	                	 dSalesTableLine.setSupplier(rs.getString("supplier"));
//	                	 dSalesTableLine.setTotalQuantity(rs.getString("totalQuantity"));
//	                	 dSalesTableLine.setDealID("" + rs.getInt("dealID"));
//	                	 dSalesTableLine.setType(rs.getString("type"));
//	                	 dSalesTableLine.setAmanat(rs.getString("amanat"));
//	                }
//		   });
//	      return dSalesTableLine;
		
	}
	
	
	 @Override
		public Long getNextTransIdForFreshEntry() throws SQLException {
		 
		 initTemplate();
			String sql = "select max(id) from arrival";
			return template.query(sql, SingleColumnRowMapper.newInstance(Long.class)).get(0);
			
			
			
//	        ResultSet set = getResult("select max(id) from arrival ;");
//	        if (!set.next()) {
//	            // Looks like there are no rows, so it seems to be first entry
//	            return 0;
//	        }
//	        set.close();
//	        return set.getInt(1);
	    }

	public DSalesTableDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntries()
	 */
	@Override
	public List<DSalesTableLine> getSalesEntries() throws SQLException, NoSuchElementException {
		initTemplate();
		return template.query(
				"SELECT * FROM arrival;",
				mapper);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntryLineByDealId(int)
	 */
	@Override
	public DSalesTableLine getSalesEntryLineByDealId(Long dealid) throws SQLException, NoSuchElementException {
		
		
		initTemplate();
		String sql = "select * from arrival where dealID= ?";
		List<DSalesTableLine> list = template.query(sql, mapper , dealid);
		if (list.isEmpty()){
			return null;
		}
		else {
			return list.get(0);
		}
		
//		String sql = "select * from arrival where dealID= ?";
//	      final DSalesTableLine dSalesTableLine = new DSalesTableLine();
//	      template.query(sql,new Object[] {dealid}, 
//	            new RowCallbackHandler() {
//	                 public void processRow(ResultSet rs) throws SQLException {
//	                	
//	                	 dSalesTableLine.setFruit(rs.getString("fruit"));
//	                	 dSalesTableLine.setDate(rs.getString("date"));
//	                	 dSalesTableLine.setGross(rs.getString("gross"));
//	                	 dSalesTableLine.setAgent(rs.getString("fwagent"));
//	                	 dSalesTableLine.setChallan(rs.getString("challan"));
//	                	 dSalesTableLine.setHalfCase(rs.getString("halfCase"));
//	                	 dSalesTableLine.setFullCase(rs.getString("fullCase"));
//	                	 dSalesTableLine.setTruck(rs.getString("truck"));
//	                	 dSalesTableLine.setDriver(rs.getString("driver"));
//	                	 dSalesTableLine.setCharges(rs.getString("charges"));
//	                	 dSalesTableLine.setRemarks(rs.getString("remarks"));
//	                	 dSalesTableLine.setNet(rs.getString("net"));
//	                	 dSalesTableLine.setSupplier(rs.getString("supplier"));
//	                	 dSalesTableLine.setTotalQuantity(rs.getString("totalQuantity"));
//	                	 dSalesTableLine.setDealID("" + rs.getInt("dealID"));
//	                	 dSalesTableLine.setType(rs.getString("type"));
//	                	 dSalesTableLine.setAmanat(rs.getString("amanat"));
//	                }
//		   });
//	      return dSalesTableLine;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IDsalesTableDao#getSalesEntryLineBySupplierName(java
	 * .lang.String)
	 */
	@Override
	public List<DSalesTableLine> getSalesEntryLineBySupplierName(String supplier)
			throws SQLException, NoSuchElementException {
		initTemplate();
		return template.query(
				"select * from arrival where supplier = ? ;",
				mapper , supplier);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;

		template = new JdbcTemplate(dataSource);
	}

	
	@Override
	public List<DSalesTableLine> getFuitByTypeAndYear(ArrivalSelectionFilter filter) {
		initTemplate();
		return filter == null ? template.query("select * from arrival ", mapper)
				: template.query("select * from arrival where fruit = ? and extract('year' from date)= ? ", mapper,
						filter.getFruit(), filter.getYear());
	}

	@Override
	public List<ArrivalSelectionFilter> getSelectionFiler() {
		initTemplate();
		return template.query(
				"select DISTINCT fruit, extract('year' from date) as year from arrival group by fruit , year;",
				selectionFilerMapper);
	}
	
	@Override
	public List<ArrivalSelectionBar> getSelectionBarchart() {
		initTemplate();
		return template.query(
				"select  sum(cast(net as int)) as sommenet, extract('year' from date) as year from arrival group by year;",
				selectionFilerMapperBar);
	}

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);
		}
	}

}
