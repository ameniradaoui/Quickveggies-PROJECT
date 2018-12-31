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

import com.quickveggies.entities.LadaanBijakSaleDeal;
import com.quickveggies.impl.ILadaanBijakDao;



@Component
public class LadaanBijakDao implements ILadaanBijakDao {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserUtils userDao ; 
	
	 /* (non-Javadoc)
	 * @see com.quickveggies.dao.ILadaanBijakDao#getLadBijSaleDeal(int)
	 */
	@Override
	public LadaanBijakSaleDeal getLadBijSaleDeal(int dealId) {
	        String sql = "select * from ladaanBijakSaleDeals where dealId=?;";
	        LadaanBijakSaleDeal deal = null;
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setInt(1, dealId);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                deal = prepareLadBijObjFromRs(rs);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return deal;
	    }

	    public LadaanBijakDao() {
		super();
		// TODO Auto-generated constructor stub
	}

		/* (non-Javadoc)
		 * @see com.quickveggies.dao.ILadaanBijakDao#getLadBijSaleDealsForBuyer(java.lang.String)
		 */
		@Override
		public List<LadaanBijakSaleDeal> getLadBijSaleDealsForBuyer(String buyerTitle) {
	        String sql = "select * from  ladaanBijakSaleDeals where dealID in (select dealID from buyerDeals where buyerTitle = ?);";
	        List<LadaanBijakSaleDeal> list = new ArrayList<>();
	        LadaanBijakSaleDeal deal = null;
	        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
	            ps.setString(1, buyerTitle);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) {
	                deal = prepareLadBijObjFromRs(rs);
	                list.add(deal);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return list;
	    }

	    private LadaanBijakSaleDeal prepareLadBijObjFromRs(ResultSet rs) throws SQLException {
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
	    public DataSource getDataSource() {
			return dataSource;
		}

		public void setDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
		}
		private static final String QRY_DEALS_FOR_LADAAN_BUYER = "select dealID from buyerDeals where buyerTitle in (select title from buyers1 where RTRIM(LTRIM(LOWER(buyerType))) IN ('ladaan', 'bijak'))";

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.ILadaanBijakDao#getNonEditedLadaanEntries()
		 */
	    @Override
		public int getNonEditedLadaanEntries() {
	        int count = 0;
	        try {
	            ResultSet rs = userDao.getResult(QRY_DEALS_FOR_LADAAN_BUYER);
	            List<Integer> buyerDealIds = new ArrayList<>();
	            while (rs.next()) {
	                buyerDealIds.add(rs.getInt(1));
	            }
	            if (buyerDealIds.isEmpty()) {
	                return 0;
	            }
	            String ladanSql = "select count(*) from  ladaanBijakSaleDeals where dealID in (";
	            for (int idx = 0; idx < buyerDealIds.size(); idx++) {
	                Integer dealId = buyerDealIds.get(idx);
	                ladanSql = ladanSql.concat(" " + dealId);
	                if (idx < buyerDealIds.size() - 1) {
	                    ladanSql = ladanSql.concat(",");
	                }
	            }
	            ladanSql = ladanSql.concat(");");
	            // System.out.println(ladanSql);
	            ResultSet ladanRs = userDao.getResult(ladanSql);
	            int ladaanCount = 0;
	            if (ladanRs.next()) {
	                ladaanCount = ladanRs.getInt(1);
	            }
	            count = buyerDealIds.size() - ladaanCount;

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return count;
	    }

		
}
