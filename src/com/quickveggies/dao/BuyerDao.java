package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.AccountEntryLine;
import com.quickveggies.entities.AccountEntryPayment;
import com.quickveggies.entities.ArrivalSelectionFilter;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.BuyerSelectionFilter;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.DSalesTableLine;
import com.quickveggies.entities.DSupplierTableLine;
import com.quickveggies.entities.ExpenseInfo;
import com.quickveggies.entities.Supplier;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IUserUtils;

@Component
public class BuyerDao implements IBuyerDao {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private AuditDao auditDao;

	@Autowired
	private UserUtils userUtils;
	
	
	
	private SimpleJdbcInsert insert;
	private void initInsert() {
		if (insert == null) {
			createInsert();
			
		}
	}
	
	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("buyers1").usingGeneratedKeyColumns("id");
	}

	

	
	public void setDataSource(DataSource dataSource) {
			template = new JdbcTemplate(dataSource);
			createInsert();
			
		}

	
	private static RowMapper<BuyerSelectionFilter> selectionFilerMapper = new RowMapper<BuyerSelectionFilter>() {

		@Override
		public BuyerSelectionFilter mapRow(ResultSet rs, int rowNum) throws SQLException {
			BuyerSelectionFilter ret = new BuyerSelectionFilter();
			ret.setTitle(rs.getString(1));
			
			return ret;
		}

	};


	private  RowMapper<Buyer> mapper2 = new RowMapper<Buyer>() {

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

	
	private static RowMapper<ExpenseInfo> ExpenseInfoMapper = new RowMapper<ExpenseInfo>() {
		@Override
		public ExpenseInfo mapRow(ResultSet data, int index) throws SQLException {
			ExpenseInfo item = new ExpenseInfo();
			item.setId(data.getLong("id"));
			item.setName(data.getString("name"));
			item.setType(data.getString("type"));
			item.setDefaultAmount(data.getString("defaultamount"));
			
			return item;
		}
	};
	
	

	private static RowMapper<DBuyerTableLine> mapper = new RowMapper<DBuyerTableLine>() {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBuyerDao#deleteBuyerExpenseInfo(java.lang.String)
	 */
	@Override
	public void deleteBuyerExpenseInfo(String name) {
		initTemplate();
		String SQL = "DELETE FROM buyerExpenseInfo  WHERE name = ?";

		try {
			template.update(SQL, name);
			auditDao.insertAuditRecord(new AuditLog(0l, userUtils.getCurrentUser(), null,
					"DELETED buyer exepense info :".concat(name), null, 0l));
			
		} catch (RuntimeException runtimeException) {

			System.err.println(runtimeException);
			throw runtimeException;
		}
		
		
//		try ( Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection
//				.prepareStatement("DELETE FROM buyerExpenseInfo  WHERE name = ?")) {
//			ps.setString(1, name);
//			ps.executeUpdate();
//			auditDao.insertAuditRecord(new AuditLog(0l, userUtils.getCurrentUser(), null,
//					"DELETED buyer exepense info :".concat(name), null, 0l));
//			ps.close();
//			connection.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerDealEntryLineFromSql(int)
	 */
	@Override
	public DBuyerTableLine getBuyerDealEntryLineFromSql(int id) throws SQLException, NoSuchElementException {
		
		
		initTemplate();
		
		List<DBuyerTableLine> list= template.query("select * from buyerDeals where id= ?", mapper , id);
		if (list.isEmpty()){
			return null;
		}
		else {
			return list.get(0);
		}
//		aaaa
//		ResultSet set = getResult("select * from buyerDeals where id='" + id + "';");
//		String title, date, sum, boxes, rate, amountReceived, dealID, aggregatedAmt, fruit, qualityType, boxSizeType;
//		if (set.next()) {
//			title = set.getString("buyerTitle");
//			date = set.getString("dealDate");
//			rate = set.getString("buyerRate");
//			sum = set.getString("buyerPay");
//			boxes = set.getString("boxes");
//			amountReceived = set.getString("amountReceived");
//			dealID = set.getInt("dealID") + "";
//			aggregatedAmt = set.getString("aggregatedAmount");
//			qualityType = set.getString("qualityType");
//			boxSizeType = set.getString("boxSizeType");
//			fruit = set.getString("fruit");
//			set.close();
//
//			return new String[] { "" + id, title, date, rate, sum, boxes, amountReceived, aggregatedAmt, dealID, fruit,
//					qualityType, boxSizeType };
//		} else {
//			throw new NoSuchElementException();
//		}
	}

//	private ResultSet getResult(String query) throws SQLException {
//		Statement statement = dataSource.getConnection().createStatement();
//		ResultSet resultSet = statement.executeQuery(query);
//		
//		return resultSet;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerDealEntry(int)
	 */
	@Override
	public DBuyerTableLine getBuyerDealEntry(Long id) throws SQLException, NoSuchElementException {
		

		initTemplate();
		
		List<DBuyerTableLine> list= template.query("select * from buyerDeals where id= ?", mapper , id);
		if (list.isEmpty()){
			return null;
		}
		else {
			return list.get(0);
		}
		
		
//		ResultSet set = getResult("select * from buyerDeals where id='" + id + "';");
//		String title, date, sum, boxes, rate, amountReceived, dealID, aggregatedAmt, fruit, qualityType, boxSizeType;
//		if (set.next()) {
//			title = set.getString("buyerTitle");
//			date = set.getString("dealDate");
//			rate = set.getString("buyerRate");
//			sum = set.getString("buyerPay");
//			boxes = set.getString("boxes");
//			amountReceived = set.getString("amountReceived");
//			dealID = set.getInt("dealID") + "";
//			aggregatedAmt = set.getString("aggregatedAmount");
//			qualityType = set.getString("qualityType");
//			boxSizeType = set.getString("boxSizeType");
//			fruit = set.getString("fruit");
//			set.close();
//			return new DBuyerTableLine(title, date, rate, sum, boxes, "" + set.getInt("id"), amountReceived,
//					aggregatedAmt, dealID, fruit, qualityType, boxSizeType);
//		} else {
//			throw new NoSuchElementException();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerDealEntries(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public List<DBuyerTableLine> getBuyerDealEntries(String buyerTitle, String[] skipBuyers)
			throws SQLException, NoSuchElementException {
		
		
		initTemplate();
		//List<DBuyerTableLine> lines = new ArrayList<>();
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
		query = query +  " ORDER BY dealDate;" ;
		
	 return template.query(query , mapper);
//		ResultSet set = getResult(query + " ORDER BY dealDate;");
//		String title, date, sum, boxes, rate, amountReceived, dealID, aggregatedAmt, fruit, qualityType, boxSizeType;
//		while (set.next()) {
//			title = set.getString("buyerTitle");
//			date = set.getString("dealDate");
//			rate = set.getString("buyerRate");
//			sum = set.getString("buyerPay");
//			boxes = set.getString("boxes");
//			amountReceived = set.getString("amountReceived");
//			dealID = set.getInt("dealID") + "";
//			aggregatedAmt = set.getString("aggregatedAmount");
//			qualityType = set.getString("qualityType");
//			boxSizeType = set.getString("boxSizeType");
//			fruit = set.getString("fruit");
//			lines.add(new DBuyerTableLine(title, date, rate, sum, boxes, "" + set.getInt("id"), amountReceived,
//					aggregatedAmt, dealID, fruit, qualityType, boxSizeType));
//		}
//		set.close();
//		return lines;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerExpenseInfoList()
	 */
	@Override
	public List<ExpenseInfo> getBuyerExpenseInfoList() {
		
		

		initTemplate();
		
		return template.query("Select * from buyerExpenseInfo order by name", ExpenseInfoMapper);}
		
//		        List<ExpenseInfo> list = new ArrayList<>();
//		        try {
//		            ResultSet rs = getResult("Select * from buyerExpenseInfo order by name");
//		            while (rs.next()) {
//		                ExpenseInfo ei = new ExpenseInfo();
//		                ei.setId(rs.getLong("id"));
//		                ei.setName(rs.getString("name"));
//		                ei.setType(rs.getString("type"));
//		                ei.setDefaultAmount(rs.getString("defaultAmount"));
//		                list.add(ei);
//		                
//		            }
//		            rs.close();
//		        } catch (SQLException e) {
//		            e.printStackTrace();
//		        }
//		        return list;
//		    }
//	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.IBuyerDao#saveBuyer(com.quickveggies.entities.Buyer)
	 */
	@Override
	public void saveBuyer(Buyer item) throws SQLException {
		initInsert();
		
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("firstName", item.getFirstName());
		args.put("lastName", item.getLastName());
		args.put("title", item.getTitle());
		args.put("company", item.getCompany());
		args.put("proprietor", item.getProprietor());
		args.put("mobile", item.getMobile());
		args.put("mobile2", item.getMobile2());
		args.put("email", item.getEmail());
		args.put("shopno", item.getShopno());
		args.put("city", item.getCity());
		args.put("email2", item.getEmail2());
		args.put("parentCompany", item.getParentCompany());
		
		args.put("creditPeriod", item.getPaymentMethod());
		args.put("paymentMethod", item.getPaymentMethod());
		args.put("buyerType", item.getBuyerType());
		args.put("photo", item.getImageStream());
		
		Long id = getRowsNum("buyers1");
		//Long genId = (long) getGeneratedKey(statement);
		Long genId = insert.executeAndReturnKey(args).longValue();
		String title = String.valueOf(id).concat(" ").concat(item.getFirstName());
		auditDao.insertAuditRecord(
			new AuditLog(0l, userUtils.getCurrentUser(), null, "ADDED new buyer:".concat(title), "buyers1", genId));
	
		
		
//		
//		String firstName = buyer.getFirstName();
//		int id = getRowsNum("buyers1");
//		String title = String.valueOf(id).concat(" ").concat(firstName);
//		String lastName = buyer.getLastName();
//
//		try {// check if buyer exists
//			this.getBuyerByName(title);
//			GeneralMethods.errorMsg("Saving failed - title already exists in database");
//			return;
//		} catch (NoSuchElementException e) {
//		}
//
//		String company = buyer.getCompany();
//		String proprietor = buyer.getProprietor();
//		String mobile = buyer.getMobile();
//		String mobile2 = buyer.getMobile2();
//		String email = buyer.getEmail();
//		String shopno = buyer.getShopno();
//		String city = buyer.getCity();
//		String email2 = buyer.getEmail2();
//		String parentCompany = buyer.getParentCompany();
//		int paymentMethod = buyer.getPaymentMethod();
//		String creditPeriod = buyer.getPaymentMethod();
//		String buyerType = buyer.getBuyerType();
//		// Statement execStmt = connection.createStatement();
//		// execStmt.execute("SET IDENTITY_INSERT buyers1 ON");
//		String sql = "INSERT INTO buyers1"
//				+ " (title,firstName,lastName,company,proprietor,mobile,mobile2,email,shopno,city,email2,parentCompany,creditPeriod,paymentMethod,buyerType,photo) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		PreparedStatement statement = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//		statement.setString(1, title);
//		statement.setString(2, firstName);
//		statement.setString(3, lastName);
//		statement.setString(4, company);
//		statement.setString(5, proprietor);
//		statement.setString(6, mobile);
//		statement.setString(7, mobile2);
//		statement.setString(8, email);
//		statement.setString(9, shopno);
//		statement.setString(10, city);
//		statement.setString(11, email2);
//		statement.setString(12, parentCompany);
//		statement.setString(13, creditPeriod);
//		statement.setInt(14, paymentMethod);
//		statement.setString(15, buyerType);
//		statement.setBlob(16, buyer.getImageStream());
//		statement.executeUpdate();
//		Long genId = (long) getGeneratedKey(statement);
//		auditDao.insertAuditRecord(
//				new AuditLog(0l, userUtils.getCurrentUser(), null, "ADDED new buyer:".concat(title), "buyers1", genId));
//		
//		statement.close();

	}

//	private static int getGeneratedKey(PreparedStatement ps) throws SQLException {
//		ResultSet genKeyRs = ps.getGeneratedKeys();
//		if (genKeyRs != null && genKeyRs.next()) {
//			return genKeyRs.getInt(1);
//		}
//		System.err.println(String.format("Unable to get the generated id for the object %s, setting it to 0",
//				ps.getMetaData().getTableName(1)));
//		return 0;
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerByName(java.lang.String)
	 */
	@Override
	public Buyer getBuyerByName(String name) throws SQLException, NoSuchElementException {
		
       initTemplate();
		
		List<Buyer> list= template.query("select * from buyers1 where title= ?", mapper2 , name);
		if (list.isEmpty()){
			return null;
		}
		else {
			return list.get(0);
		}
		
//		ResultSet rs = getResult("select * from buyers1 where title='" + name + "';");
//		// System.out.print("buyer title="+name+"\n");
//		if (rs.next()) {
//			Long id = rs.getLong("id");
//			String title = name;
//			String firstName = rs.getString("firstName");
//			String lastName = rs.getString("lastName");
//			String company = rs.getString("company");
//			String proprietor = rs.getString("proprietor");
//			String mobile = rs.getString("mobile");
//			String mobile2 = rs.getString("mobile2");
//			String email = rs.getString("email");
//			String shopno = rs.getString("shopno");
//			String city = rs.getString("city");
//			String email2 = rs.getString("email2");
//			String parentCompany = rs.getString("parentCompany");
//			String paymentMethod = rs.getString("paymentMethod");
//			String creditPeriod = rs.getString("creditPeriod");
//			String buyerType = rs.getString("buyerType");
//
//			Buyer receivedBuyer = new Buyer(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
//					shopno, city, email2, parentCompany, getPayementMethod(paymentMethod), creditPeriod, buyerType);
//			Blob photo = null;
//			try {
//				photo = rs.getBlob("photo");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				// e.printStackTrace();
//			}
//			if (photo != null) {
//				receivedBuyer.setImageStream(photo.getBinaryStream());
//			}
//			rs.close();
//			return receivedBuyer;
//		} else {
//			return null;
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyerById(int)
	 */
	@Override
	public Buyer getBuyerById(Long id) throws SQLException, NoSuchElementException {
		
		  initTemplate();
			
			List<Buyer> list= template.query("select * from buyers1 where id= ?", mapper2 , id);
			if (list.isEmpty()){
				return null;
			}
			else {
				return list.get(0);
			}
		
		
//		ResultSet rs = getResult("select * from buyers1 where id='" + id + "';");
//		if (rs.next()) {
//			String title = rs.getString("title");
//			String firstName = rs.getString("firstName");
//			String lastName = rs.getString("lastName");
//			String company = rs.getString("company");
//			String proprietor = rs.getString("proprietor");
//			String mobile = rs.getString("mobile");
//			String mobile2 = rs.getString("mobile2");
//			String email = rs.getString("email");
//			String shopno = rs.getString("shopno");
//			String city = rs.getString("city");
//			String email2 = rs.getString("email2");
//			String parentCompany = rs.getString("parentCompany");
//			String paymentMethod = rs.getString("paymentMethod");
//			String creditPeriod = rs.getString("creditPeriod");
//			String buyerType = rs.getString("buyerType");
//
//			Buyer receivedBuyer = new Buyer(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
//					shopno, city, email2, parentCompany, getPayementMethod(paymentMethod), creditPeriod, buyerType);
//			Blob photo = rs.getBlob("photo");
//			if (photo != null) {
//				receivedBuyer.setImageStream(photo.getBinaryStream());
//			}
//			rs.close();
//			return receivedBuyer;
//		} else {
//			throw new NoSuchElementException("No Buyer user in database");
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getBuyers()
	 */
	@Override
	public List<Buyer> getBuyers() throws SQLException {
		
		initTemplate();
		
		return template.query("select * from buyers1", mapper2 );
	
		}
	
//		List<Buyer> list = new ArrayList<>();
//		ResultSet rs = getResult("select * from buyers1;");
//		while (rs.next()) {
//			Long id = rs.getLong("id");
//			String title = rs.getString("title");
//			String firstName = rs.getString("firstName");
//			String lastName = rs.getString("lastName");
//			String company = rs.getString("company");
//			String proprietor = rs.getString("proprietor");
//			String mobile = rs.getString("mobile");
//			String mobile2 = rs.getString("mobile2");
//			String email = rs.getString("email");
//			String shopno = rs.getString("shopno");
//			String city = rs.getString("city");
//			String email2 = rs.getString("email2");
//			String parentCompany = rs.getString("parentCompany");
//			String paymentMethod = rs.getString("paymentMethod");
//			String creditPeriod = rs.getString("creditPeriod");
//			String buyerType = rs.getString("buyerType");
//
//			Buyer receivedBuyer = new Buyer(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
//					shopno, city, email2, parentCompany, getPayementMethod(paymentMethod), creditPeriod, buyerType);
//
//			Blob photo = rs.getBlob("photo");
//			if (photo != null) {
//				receivedBuyer.setImageStream(photo.getBinaryStream());
//			}
//			list.add(receivedBuyer);
//		}
//		rs.close();
//		return list;
	

	private int getPayementMethod(String paymentMethod) {
		try {
			return Integer.parseInt(paymentMethod);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getRowsNum(java.lang.String)
	 */
	@Override
	public Long getRowsNum(String tablename) {
		
	
		
			return template.query("SELECT COUNT(*) FROM " + tablename, SingleColumnRowMapper.newInstance(Long.class)).get(0);
		
		
//		int rowsNum = 0;
//		try {
//			ResultSet result = getResult("SELECT COUNT(*) FROM " + tablename);
//			while (result.next()) {
//				rowsNum = result.getInt(1);
//			}
//		} catch (SQLException e) {
//			System.out.print("sql exception in getRowsNum\n");
//		}

		//return rowsNum;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.IBuyerDao#getAllBuyers()
	 */
	//@Override
//	public Vector<Vector<Object>> getAllBuyers() throws SQLException {
//		Vector<Vector<Object>> objects = new Vector<>();
//		
//		
//		ResultSet resultSet = dataSource.getConnection().prepareStatement("SELECT * FROM buyers1").executeQuery();
//		while (resultSet.next()) {
//			Vector<Object> vector = new Vector<>();
//			for (int i = 1; i <= 15; i++) {
//				vector.add(resultSet.getObject(i));
//			}
//			objects.add(vector);
//		}
//		return objects;
//	}

	public BuyerDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<DBuyerTableLine> getListByBuyerType(String title) {
		initTemplate();
		if ((title == null) || (title.trim().length() == 0)) {
			return template.query("select * from buyerdeals ", mapper);
		}
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("title", "%" + title + "%");
		List<DBuyerTableLine> list = template.query("select * from buyerdeals where buyertitle like ? ", mapper, "%" + title.trim() + "%");
		return list;

	}
	@Override
	public List<Buyer> getListByBuyerTitle(String name) {
		initTemplate();
		if ((name == null) || (name.trim().length() == 0)) {
			return template.query("select * from buyers1 ", mapper2);
		}
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("title", "%" + title + "%");
		List<Buyer> list = template.query("select * from buyers1 where title like ? ", mapper2, "%" + name.trim() + "%");
		return list;

	}


	@Override
	public List<Buyer> getBuyerByDate(BuyerSelectionFilter filter) {
		return filter == null ? template.query("select * from buyers1 ", mapper2)
				: template.query("select * from buyers1 where title = ? ", mapper2,
						filter.getTitle());
	}

	@Override
	public List<BuyerSelectionFilter> getSelectionFiler() {
		initTemplate();
		return template.query(
				"select DISTINCT title from buyers1 ",
				selectionFilerMapper);
	}
	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);
			
		}
	}

	
}