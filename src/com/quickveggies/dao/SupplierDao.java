package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.DSupplierTableLine;
import com.quickveggies.entities.Supplier;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class SupplierDao implements ISupplierDao {
	
	

	@Autowired
	private AuditDao ad ;
	@Autowired
	private DatabaseClient db;
	@Autowired
	private UserUtils userUtils ;
	
	@Autowired
	private  DSalesTableDao dtd ;
	
	@Autowired
	private  DataSource dataSource;
	public DataSource getDataSource() {
		return dataSource;
	}


	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public List<String[]> getSupplierDealEntryLines(String title) throws SQLException {
        List<String[]> lines = new ArrayList<>();
        String query = "SELECT * FROM supplierDeals ";
        if (title != null && !title.isEmpty()) {
            query += " WHERE supplierTitle='" + title + "' ";
        }
        ResultSet set = getResult(query + ";");
        String supplierTitle, date, rate, net, cases, agent, amountReceived, dealStr, fruit, qualityType, boxSizeType;
        while (set.next()) {
            supplierTitle = set.getString("supplierTitle");
            if (supplierTitle == null || "".equals(supplierTitle)) {
                continue;
            }
            date = set.getString("date");
            rate = set.getString("supplierRate");
            net = set.getString("net");
            cases = set.getString("cases");
            agent = set.getString("agent");
            amountReceived = set.getString("amountReceived");
            qualityType = set.getString("qualityType");
            boxSizeType = set.getString("boxSizeType");
            fruit = set.getString("fruit");
            int dealId = set.getInt("dealID");
            String proprietor = getSupplierByName(supplierTitle).getProprietor();
            String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
            dealStr = dealId + "";
            lines.add(new String[] {"" + set.getInt("id"), supplierTitle.trim(), date,
                rate, net, cases, agent, amountReceived, dealStr, proprietor,
                amanat, fruit, qualityType, boxSizeType});
        }
        return lines;
    }
    
	
	public SupplierDao() {
		super();
		// TODO Auto-generated constructor stub
	}


	/* (non-Javadoc)
	 * @see com.quickveggies.dao.ISupplierDao#getSupplierDealEntries(java.lang.String)
	 */
	@Override
	public List<DSupplierTableLine> getSupplierDealEntries(String title) throws SQLException {
        List<DSupplierTableLine> lines = new ArrayList<>();
        String query = "SELECT * FROM supplierDeals ";
        if (title != null && !title.isEmpty()) {
            query += " WHERE supplierTitle='" + title + "' ";
        }
        ResultSet set = getResult(query + ";");
        String supplierTitle, date, rate, net, cases, agent, amountReceived, dealStr, fruit, qualityType, boxSizeType;
        while (set.next()) {
            supplierTitle = set.getString("supplierTitle");
            if (supplierTitle == null || "".equals(supplierTitle)) {
                continue;
            }
            date = set.getString("date");
            rate = set.getString("supplierRate");
            net = set.getString("net");
            cases = set.getString("cases");
            agent = set.getString("agent");
            amountReceived = set.getString("amountReceived");
            qualityType = set.getString("qualityType");
            boxSizeType = set.getString("boxSizeType");
            fruit = set.getString("fruit");
            int dealId = set.getInt("dealID");
            String proprietor = getSupplierByName(supplierTitle).getProprietor();
            String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
            lines.add(new DSupplierTableLine("" + set.getInt("id"), supplierTitle.trim(), date,
                rate, net, cases, agent, amountReceived, dealId + "", proprietor,
                amanat, fruit, qualityType, boxSizeType));
        }
        return lines;
    }
  
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.ISupplierDao#getSupplierDealEntry(int)
		 */
	    @Override
		public DSupplierTableLine getSupplierDealEntry(int id) throws SQLException {
	        String query = "SELECT * FROM supplierDeals WHERE id=" + id + ";";
	        ResultSet set = getResult(query);
	        String supplierTitle, date, rate, net, cases, agent, amountReceived,
	                fruit, qualityType, boxSizeType;
	        if (set.next()) {
	            supplierTitle = set.getString("supplierTitle").trim();
	            date = set.getString("date");
	            rate = set.getString("supplierRate");
	            net = set.getString("net");
	            cases = set.getString("cases");
	            agent = set.getString("agent");
	            amountReceived = set.getString("amountReceived");
	            qualityType = set.getString("qualityType");
	            boxSizeType = set.getString("boxSizeType");
	            fruit = set.getString("fruit");
	            int dealId = set.getInt("dealID");
	            String proprietor = getSupplierByName(supplierTitle).getProprietor();
	            String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
	            return new DSupplierTableLine("" + set.getInt("id"), supplierTitle,
	                    date, rate, net, cases, agent, amountReceived, dealId + "",
	                    proprietor, amanat, fruit, qualityType, boxSizeType);
	        }
	        else {
	            throw new NoSuchElementException("Supplier deal isn't found");
	        }
	    }

	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.ISupplierDao#getRowsNum(java.lang.String)
		 */
	    @Override
		public int getRowsNum(String tablename) {
	        int rowsNum = 0;
	        try {
	            ResultSet result = getResult("SELECT COUNT(*) FROM " + tablename);
	            while (result.next()) {
	                rowsNum = result.getInt(1);
	            }
	        } catch (SQLException e) {
	            System.out.print("sql exception in getRowsNum\n");
	        }
	        return rowsNum;
	 }
   
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.ISupplierDao#saveSupplier(com.quickveggies.entities.Supplier)
	 */
    @Override
	public void saveSupplier(Supplier supplier) throws SQLException {
        String firstName = supplier.getFirstName();
        int id = getRowsNum("suppliers1");
        String title = String.valueOf(id).concat(" ").concat(firstName);
        try {// check if supplier exists
            this.getSupplierByName(title);
            GeneralMethods.errorMsg("Saving failed - title already exists in database");
            return;
        } catch (NoSuchElementException e) {
        }
        String lastName = supplier.getLastName();
        String company = supplier.getCompany();
        String proprietor = supplier.getProprietor();
        String mobile = supplier.getMobile();
        String mobile2 = supplier.getMobile2();
        String email = supplier.getEmail();
        String village = supplier.getVillage();
        String po = supplier.getPo();
        String tehsil = supplier.getTehsil();
        String ac = supplier.getAc();
        String bank = supplier.getBank();
        String ifsc = supplier.getIfsc();
        String sql = "insert into suppliers1 (title,firstName,lastName,company,proprietor,mobile,mobile2,email,village,po,tehsil,ac,bank,ifsc, photo) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement statement = dataSource.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, title);
        statement.setString(2, firstName);
        statement.setString(3, lastName);
        statement.setString(4, company);
        statement.setString(5, proprietor);
        statement.setString(6, mobile);
        statement.setString(7, mobile2);
        statement.setString(8, email);
        statement.setString(9, village);
        statement.setString(10, po);
        statement.setString(11, tehsil);
        statement.setString(12, ac);
        statement.setString(13, bank);
        statement.setString(14, ifsc);
        statement.setBlob(15, supplier.getImageStream());

        statement.executeUpdate();
        ad.insertAuditRecord(
                new AuditLog(0, userUtils.getCurrentUser(), null, "ADDED new entry for supplier :".concat(title), "Suppliers1", db.getGeneratedKey(statement)));

    }
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.ISupplierDao#getSupplierById(int)
	 */
    @Override
	public Supplier getSupplierById(int id) throws SQLException, NoSuchElementException {
        ResultSet rs = getResult("select * from suppliers1 where id='" + id + "';");
        if (rs.next()) {
            String title = rs.getString("title");
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            String company = rs.getString("company");
            String proprietor = rs.getString("proprietor");
            String mobile = rs.getString("mobile");
            String mobile2 = rs.getString("mobile2");
            String email = rs.getString("email");
            String village = rs.getString("village");
            String po = rs.getString("po");
            String tehsil = rs.getString("tehsil");
            String ac = rs.getString("ac");
            String bank = rs.getString("bank");
            String ifsc = rs.getString("ifsc");

            Supplier receivedSupplier = new Supplier(id, title, firstName, lastName, company, proprietor, mobile,
                    mobile2, email, village, po, tehsil, ac, bank, ifsc);
            Blob photo = rs.getBlob("photo");
            if (photo != null) {
                receivedSupplier.setImageStream(photo.getBinaryStream());
            }
            return receivedSupplier;
        }
        else {
            throw new NoSuchElementException("No supplier user in database");
        }
    }

    private ResultSet getResult(String query) throws SQLException {
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }
    public   Supplier getSupplierByName(String name) throws SQLException, NoSuchElementException {
        ResultSet rs = getResult("select * from suppliers1 where title='" + name + "';");
        if (rs.next()) {
            Integer id = rs.getInt("id");
            String title = name;
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            String company = rs.getString("company");
            String proprietor = rs.getString("proprietor");
            String mobile = rs.getString("mobile");
            String mobile2 = rs.getString("mobile2");
            String email = rs.getString("email");
            String village = rs.getString("village");
            String po = rs.getString("po");
            String tehsil = rs.getString("tehsil");
            String ac = rs.getString("ac");
            String bank = rs.getString("bank");
            String ifsc = rs.getString("ifsc");

            Supplier receivedSupplier = new Supplier(id, title, firstName, lastName, company, proprietor, mobile,
                    mobile2, email, village, po, tehsil, ac, bank, ifsc);
            Blob photo = null;
			try {
				photo = rs.getBlob("photo");
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}
            if (photo != null) {
                receivedSupplier.setImageStream(photo.getBinaryStream());
            }
            return receivedSupplier;
        }
        else {
            throw new NoSuchElementException("No Supplier user in database");
        }
    }




   

}
