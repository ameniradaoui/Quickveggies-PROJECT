package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.controller.ChargeTypeValueMap;
import com.quickveggies.entities.Charge;
import com.quickveggies.impl.IChargesDao;



@Component
public class ChargesDao implements IChargesDao {

	@Autowired
	private DataSource dataSource;
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IChargesDao#addDealCharges(java.util.Map, int)
	 */
	@Override
	public void addDealCharges(Map<String, ChargeTypeValueMap> map, int dealID) {
        String chargeInsertQry = "Insert into charges (chargeName, value, chargeType, chargeRate) VALUES (?, ?,?,?)";
        String chargeDealEntryQuery = "Insert into dealCharges (chargeID,dealID) VALUES (?, ?) ";
        for (String chargeName : map.keySet()) {
            try (PreparedStatement ps1 = dataSource.getConnection().prepareStatement(chargeInsertQry, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement ps2 = dataSource.getConnection().prepareStatement(chargeDealEntryQuery);) {
                ps1.setString(1, chargeName);
                ps1.setString(2, map.get(chargeName).totalValue.getText().toLowerCase());
                ps1.setString(3, map.get(chargeName).type.getValue().toLowerCase());
                ps1.setString(4, map.get(chargeName).rate.getText().trim());
                ps1.executeUpdate();

                ResultSet rsGen = ps1.getGeneratedKeys();
                if (!rsGen.next()) {
                    throw new IllegalStateException("The database failed to return generated key for charges");
                }
                ps2.setInt(1, rsGen.getInt(1));
                ps2.setInt(2, dealID);
                ps2.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IChargesDao#getDealCharges(int)
	 */
    @Override
	public List<Charge> getDealCharges(int dealID) {
        List<Charge> list = new ArrayList<>();
        String dealChargeQuery = "select * from charges where id in (select chargeID from dealCharges where dealID = ?);";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(dealChargeQuery)) {
            ps.setInt(1, dealID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Charge chg = new Charge();
                chg.setId(rs.getInt("id"));
                chg.setName(rs.getString("chargeName"));
                chg.setAmount(rs.getString("value"));
                chg.setType(rs.getString("chargeType"));
                chg.setRate(rs.getString("chargeRate"));
                list.add(chg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


	public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public ChargesDao() {
		super();
		// TODO Auto-generated constructor stub
	}


	
}
