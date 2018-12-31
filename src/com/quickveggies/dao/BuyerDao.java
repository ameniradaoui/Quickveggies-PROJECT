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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.ExpenseInfo;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class BuyerDao implements IBuyerDao {
    

	@Autowired
	private  DataSource dataSource;

	@Autowired
	private AuditDao auditDao ;
	
	@Autowired
	private UserUtils userUtils ;
	
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IBuyerDao#deleteBuyerExpenseInfo(java.lang.String)
	 */
	@Override
	public void deleteBuyerExpenseInfo(String name) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement("DELETE FROM buyerExpenseInfo  WHERE name = ?")) {
            ps.setString(1, name);
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userUtils.getCurrentUser(), null, "DELETED buyer exepense info :".concat(name), null, 0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	
	 /* (non-Javadoc)
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerDealEntryLineFromSql(int)
	 */
	@Override
	public String[] getBuyerDealEntryLineFromSql(int id) throws SQLException, NoSuchElementException {
	        ResultSet set = getResult("select * from buyerDeals where id='" + id + "';");
	        String title, date, sum, boxes, rate, amountReceived, dealID,
	                aggregatedAmt, fruit, qualityType, boxSizeType;
	        if (set.next()) {
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
	            return new String[]{"" + id, title, date, rate, sum, boxes, amountReceived,
	                aggregatedAmt, dealID, fruit, qualityType, boxSizeType};
	        }
	        else {
	            throw new NoSuchElementException();
	        }
	    }

	 private ResultSet getResult(String query) throws SQLException {
	        Statement statement = dataSource.getConnection().createStatement();
	        ResultSet resultSet = statement.executeQuery(query);
	        return resultSet;
	    }

		/* (non-Javadoc)
		 * @see com.quickveggies.dao.IBuyerDao#getBuyerDealEntry(int)
		 */
		@Override
		public DBuyerTableLine getBuyerDealEntry(int id) throws SQLException, NoSuchElementException {
	        ResultSet set = getResult("select * from buyerDeals where id='" + id + "';");
	        String title, date, sum, boxes, rate, amountReceived, dealID,
	                aggregatedAmt, fruit, qualityType, boxSizeType;
	        if (set.next()) {
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
	            return new DBuyerTableLine(title, date, rate, sum, boxes, "" + set.getInt("id"),
	                    amountReceived, aggregatedAmt, dealID, fruit, qualityType, boxSizeType);
	        }
	        else {
	            throw new NoSuchElementException();
	        }
	    }
	    
	    /* (non-Javadoc)
		 * @see com.quickveggies.dao.IBuyerDao#getBuyerDealEntries(java.lang.String, java.lang.String[])
		 */
	    @Override
		public List<DBuyerTableLine> getBuyerDealEntries(String buyerTitle,
	            String[] skipBuyers) throws SQLException, NoSuchElementException {
	        List<DBuyerTableLine> lines = new ArrayList<>();
	        String query = "SELECT * FROM buyerDeals WHERE 1=1 ";
	        if (buyerTitle != null && !buyerTitle.isEmpty()) {
	            query += " AND buyerTitle='" + buyerTitle + "' ";
	        }
	        if (skipBuyers != null && skipBuyers.length > 0) {
	            query += " AND NOT buyerTitle IN (";
	            for (String buyer : skipBuyers) {
	                query += "'" + buyer + "',";
	            }
	            query = query.substring(0, query.length() - 1);
	            query += ")";
	        }
	        ResultSet set = getResult(query + " ORDER BY dealDate;");
	        String title, date, sum, boxes, rate, amountReceived, dealID, aggregatedAmt, fruit, qualityType, boxSizeType;
	        while (set.next()) {
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
	            lines.add(new DBuyerTableLine(title, date, rate, sum, boxes, "" + set.getInt("id"),
	                amountReceived, aggregatedAmt, dealID, fruit, qualityType, boxSizeType));
	        }
	        return lines;
	    }

		/* (non-Javadoc)
		 * @see com.quickveggies.dao.IBuyerDao#getBuyerExpenseInfoList()
		 */
		@Override
		public List<ExpenseInfo> getBuyerExpenseInfoList() {
			// TODO Auto-generated method stub
			return null;
		}
		  /* (non-Javadoc)
		 * @see com.quickveggies.dao.IBuyerDao#saveBuyer(com.quickveggies.entities.Buyer)
		 */
		@Override
		public void saveBuyer(Buyer buyer) throws SQLException {
		        String firstName = buyer.getFirstName();
		        int id = getRowsNum("buyers1");
		        String title = String.valueOf(id).concat(" ").concat(firstName);
		        String lastName = buyer.getLastName();

		        try {// check if buyer exists
		            this.getBuyerByName(title);
		            GeneralMethods.errorMsg("Saving failed - title already exists in database");
		            return;
		        } catch (NoSuchElementException e) {
		        }

		        String company = buyer.getCompany();
		        String proprietor = buyer.getProprietor();
		        String mobile = buyer.getMobile();
		        String mobile2 = buyer.getMobile2();
		        String email = buyer.getEmail();
		        String shopno = buyer.getShopno();
		        String city = buyer.getCity();
		        String email2 = buyer.getEmail2();
		        String parentCompany = buyer.getParentCompany();
		        int paymentMethod = buyer.getPaymentMethod();
		        String creditPeriod = buyer.getCreditPeriod();
		        String buyerType = buyer.getBuyerType();
		        // Statement execStmt = connection.createStatement();
		        // execStmt.execute("SET IDENTITY_INSERT buyers1 ON");
		        String sql = "INSERT INTO buyers1"
		                + " (title,firstName,lastName,company,proprietor,mobile,mobile2,email,shopno,city,email2,parentCompany,creditPeriod,paymentMethod,buyerType,photo) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		        PreparedStatement statement = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		        statement.setString(1, title);
		        statement.setString(2, firstName);
		        statement.setString(3, lastName);
		        statement.setString(4, company);
		        statement.setString(5, proprietor);
		        statement.setString(6, mobile);
		        statement.setString(7, mobile2);
		        statement.setString(8, email);
		        statement.setString(9, shopno);
		        statement.setString(10, city);
		        statement.setString(11, email2);
		        statement.setString(12, parentCompany);
		        statement.setString(13, creditPeriod);
		        statement.setInt(14, paymentMethod);
		        statement.setString(15, buyerType);
		        statement.setBlob(16, buyer.getImageStream());
		        statement.executeUpdate();
		        int genId = getGeneratedKey(statement);
		        auditDao.insertAuditRecord(new AuditLog(0, userUtils.getCurrentUser(), null, "ADDED new buyer:".concat(title), "buyers1", genId));

		    }
		  
		  private static int getGeneratedKey(PreparedStatement ps) throws SQLException {
		        ResultSet genKeyRs = ps.getGeneratedKeys();
		        if (genKeyRs != null && genKeyRs.next()) {
		            return genKeyRs.getInt(1);
		        }
		        System.err.println(String.format("Unable to get the generated id for the object %s, setting it to 0", ps.getMetaData().getTableName(1)));
		        return 0;

		    }
		  /* (non-Javadoc)
		 * @see com.quickveggies.dao.IBuyerDao#getBuyerByName(java.lang.String)
		 */
		@Override
		public Buyer getBuyerByName(String name) throws SQLException, NoSuchElementException {
		        ResultSet rs = getResult("select * from buyers1 where title='" + name + "';");
//		        System.out.print("buyer title="+name+"\n");
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
		            String shopno = rs.getString("shopno");
		            String city = rs.getString("city");
		            String email2 = rs.getString("email2");
		            String parentCompany = rs.getString("parentCompany");
		            String paymentMethod = rs.getString("paymentMethod");
		            String creditPeriod = rs.getString("creditPeriod");
		            String buyerType = rs.getString("buyerType");

		            Buyer receivedBuyer = new Buyer(id, title, firstName, lastName, company, proprietor,
		                    mobile, mobile2, email, shopno, city, email2, parentCompany, getPayementMethod(paymentMethod),
		                    creditPeriod, buyerType);
		            Blob photo = null;
					try {
						photo = rs.getBlob("photo");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
		            if (photo != null) {
		                receivedBuyer.setImageStream(photo.getBinaryStream());
		            }
		            return receivedBuyer;
		        }
		        else {
		            throw new NoSuchElementException("No Buyer user in database");
		        }
		    }
		  
		    /* (non-Javadoc)
			 * @see com.quickveggies.dao.IBuyerDao#getBuyerById(int)
			 */
		    @Override
			public Buyer getBuyerById(int id) throws SQLException, NoSuchElementException {
		        ResultSet rs = getResult("select * from buyers1 where id='" + id + "';");
		        if (rs.next()) {
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

		            Buyer receivedBuyer = new Buyer(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
		                    shopno, city, email2, parentCompany, getPayementMethod(paymentMethod), creditPeriod, buyerType);
		            Blob photo = rs.getBlob("photo");
		            if (photo != null) {
		                receivedBuyer.setImageStream(photo.getBinaryStream());
		            }

		            return receivedBuyer;
		        } else {
		            throw new NoSuchElementException("No Buyer user in database");
		        }
		    }
		    
		    /* (non-Javadoc)
			 * @see com.quickveggies.dao.IBuyerDao#getBuyers()
			 */
		    @Override
			public List<Buyer> getBuyers() throws SQLException {
		        List<Buyer> list = new ArrayList<>();
		        ResultSet rs = getResult("select * from buyers1;");
		        while (rs.next()) {
		            Integer id = rs.getInt("id");
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

		            Buyer receivedBuyer = new Buyer(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
		                    shopno, city, email2, parentCompany, getPayementMethod(paymentMethod), creditPeriod, buyerType);
		            
		            Blob photo = rs.getBlob("photo");
		            if (photo != null) {
		                receivedBuyer.setImageStream(photo.getBinaryStream());
		            }
		            list.add(receivedBuyer);
		        }
		        return list;
		    }
		    

			private int getPayementMethod(String paymentMethod) {
				try {
					return Integer.parseInt(paymentMethod);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					return 0;
				}
			}

			 /* (non-Javadoc)
			 * @see com.quickveggies.dao.IBuyerDao#getRowsNum(java.lang.String)
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

			    public DataSource getDataSource() {
				return dataSource;
			}


			public void setDataSource(DataSource dataSource) {
				this.dataSource = dataSource;
			}


				/* (non-Javadoc)
				 * @see com.quickveggies.dao.IBuyerDao#getAllBuyers()
				 */
			    @Override
				public Vector<Vector<Object>> getAllBuyers() throws SQLException {
			        Vector<Vector<Object>> objects = new Vector<>();
			        ResultSet resultSet = dataSource.getConnection().prepareStatement("SELECT * FROM buyers").executeQuery();
			        while (resultSet.next()) {
			            Vector<Object> vector = new Vector<>();
			            for (int i = 1; i <= 15; i++) {
			                vector.add(resultSet.getObject(i));
			            }
			            objects.add(vector);
			        }
			        return objects;
			    }


				public BuyerDao() {
					super();
					// TODO Auto-generated constructor stub
				}


			
}
