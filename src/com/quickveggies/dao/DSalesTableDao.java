package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.DSalesTableLine;
import com.quickveggies.impl.IDsalesTableDao;


@Component
public class DSalesTableDao implements IDsalesTableDao {
	
	@Autowired
	private DataSource dataSource;
	 /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntryLineFromSql(int)
	 */
	@Override
	public DSalesTableLine getSalesEntryLineFromSql(int id) throws SQLException, NoSuchElementException {
        ResultSet set = getResult("select * from arrival where id='" + id + "';");

        String fruit, date, challan, supplier, totalQuantity, fullCase, halfCase,
                agent, truck, driver, gross, charges, net, remarks, dealID, type, amanat;

        if (set.next()) {
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
            return new DSalesTableLine(fruit, "" + id, date, challan, supplier, totalQuantity, fullCase, halfCase,
                    agent, truck, driver, gross, charges, net, remarks, dealID, type, amanat);
        }
        else {
            throw new NoSuchElementException();
        }
    }
	
    
    public DSalesTableDao() {
		super();
		// TODO Auto-generated constructor stub
	}


	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntries()
	 */
	@Override
	public List<DSalesTableLine> getSalesEntries() throws SQLException, NoSuchElementException {
        ResultSet set = getResult("SELECT * FROM arrival;");

        String fruit, date, challan, supplier, totalQuantity, fullCase, halfCase,
                agent, truck, driver, gross, charges, net, remarks, dealID, type, amanat;
        List<DSalesTableLine> values = new ArrayList<>();

        while (set.next()) {
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
            values.add(new DSalesTableLine(fruit, "" + set.getLong("id"), date,
                    challan, supplier, totalQuantity, fullCase, halfCase, agent,
                    truck, driver, gross, charges, net, remarks, dealID, type, amanat));
        }
        return values;
    }
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntryLineByDealId(int)
	 */
    @Override
	public DSalesTableLine getSalesEntryLineByDealId(int dealid) throws SQLException, NoSuchElementException {
        ResultSet set = getResult("select * from arrival where dealID='" + dealid + "';");
        if (!set.next()) {
            throw new NoSuchElementException();
        }
        return getSalesEntryLineFromSql(set.getInt("id"));
    }
    
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.IDsalesTableDao#getSalesEntryLineBySupplierName(java.lang.String)
	 */
    @Override
	public List<DSalesTableLine> getSalesEntryLineBySupplierName(String supplier)
            throws SQLException, NoSuchElementException {
        List<DSalesTableLine> lines = new ArrayList<>();
        ResultSet set = getResult("select * from arrival where supplier='" + supplier + "';");
        while (set.next()) {
            lines.add(new DSalesTableLine(set.getString("fruit"), set.getString("id"),
                    set.getString("date"), set.getString("challan"), set.getString("supplier"),
                    set.getString("totalQuantity"), set.getString("fullCase"), set.getString("halfCase"),
                    set.getString("fwagent"), set.getString("truck"), set.getString("driver"),
                    set.getString("gross"), set.getString("charges"), set.getString("net"),
                    set.getString("remarks"), set.getString("dealId"), set.getString("type"),
                    set.getString("amanat")));
        }
        return lines;
    }

    public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	private ResultSet getResult(String query) throws SQLException {
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }

	

}
