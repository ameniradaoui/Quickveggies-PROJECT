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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.GeneralMethods;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.DBuyerTableLine;
import com.quickveggies.entities.DSupplierTableLine;
import com.quickveggies.entities.Supplier;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;

@Component
public class SupplierDao implements ISupplierDao {

	@Autowired
	private AuditDao ad;
	@Autowired
	private DatabaseClient db;
	@Autowired
	private UserUtils userUtils;

	@Autowired
	private DSalesTableDao dtd;

	@Autowired
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	

	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insert == null) {
			createInsert();

		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("suppliers1").usingGeneratedKeyColumns("id");
	}
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();

	}
	
	private RowMapper<Supplier> mapper2 = new RowMapper<Supplier>() {

		@Override
		public Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
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

			Supplier supp = new Supplier(id, title, firstName, lastName, company, proprietor, mobile, mobile2, email,
					village, po, tehsil, ac, bank, ifsc);

//			Blob photo = rs.getBlob("photo");
//			if (photo != null) {
//				Supplier.setImageStream(photo.getBinaryStream());
//			}

			return supp;
		}
	};

	private RowMapper<DSupplierTableLine> mapper = new RowMapper<DSupplierTableLine>() {

		@Override
		public DSupplierTableLine mapRow(ResultSet set, int rowNum) throws SQLException {

			String supplierTitle, date, rate, net, cases, agent, amountReceived, dealStr, fruit, qualityType,
					boxSizeType;

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
			Long dealId = set.getLong("dealID");
			String proprietor = getSupplierByName(supplierTitle).getProprietor();
			String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
			return new DSupplierTableLine("" + set.getInt("id"), supplierTitle, date, rate, net, cases, agent,
					amountReceived, dealId + "", proprietor, amanat, fruit, qualityType, boxSizeType);
		}
	};

	private JdbcTemplate template;

	public List<String[]> getSupplierDealEntryLines(String title) throws SQLException {
		initTemplate();
		List<DSupplierTableLine> list=new ArrayList<>();
		List<String[]> lines = new ArrayList<>();
		String query = "SELECT * FROM supplierDeals ";
		if (title != null && !title.isEmpty()) {
			query += " WHERE supplierTitle= ?" ;
			list = template.query(query, mapper ,title);
			
		}
		else {
			list= template.query(query, mapper);
		}
		for(DSupplierTableLine obj:list){
			lines.add(new String[] { "" + obj.getSaleNo() , obj.getSupplierTitle().trim(), obj.getDate(), obj.getSupplierRate(), obj.getNet(), 
					obj.getCases(), obj.getAgent(),
					obj.getAmountReceived(), obj.getDealID()+"", obj.getProprietor(),
					obj.getAmanat(), obj.getFruit(), obj.getQualityType(), obj.getBoxSizeType() });
			}
		
//		List<String[]> lines = new ArrayList<>();
//		String query = "SELECT * FROM supplierDeals ";
//		if (title != null && !title.isEmpty()) {
//			query += " WHERE supplierTitle='" + title + "' ";
//		}
//		ResultSet set = getResult(query + ";");
//		String supplierTitle, date, rate, net, cases, agent, amountReceived, dealStr, fruit, qualityType, boxSizeType;
//		while (set.next()) {
//			supplierTitle = set.getString("supplierTitle");
//			if (supplierTitle == null || "".equals(supplierTitle)) {
//				continue;
//			}
//			date = set.getString("date");
//			rate = set.getString("supplierRate");
//			net = set.getString("net");
//			cases = set.getString("cases");
//			agent = set.getString("agent");
//			amountReceived = set.getString("amountReceived");
//			qualityType = set.getString("qualityType");
//			boxSizeType = set.getString("boxSizeType");
//			fruit = set.getString("fruit");
//			Long dealId = set.getLong("dealID");
//			String proprietor = getSupplierByName(supplierTitle).getProprietor();
//			String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
//			dealStr = dealId + "";
//			lines.add(new String[] { "" + set.getInt("id"), supplierTitle.trim(), date, rate, net, cases, agent,
//					amountReceived, dealStr, proprietor, amanat, fruit, qualityType, boxSizeType });
//		}
//		set.close();
     	return lines;
	}

	public SupplierDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ISupplierDao#getSupplierDealEntries(java.lang.
	 * String)
	 */
	@Override
	public List<DSupplierTableLine> getSupplierDealEntries(String title) throws SQLException {
	//	List<DSupplierTableLine> lines = new ArrayList<>();
		String query = "SELECT * FROM supplierDeals ";
		if (title != null && !title.isEmpty()) {
			query += " WHERE supplierTitle= ?" ;
			return template.query(query, mapper , title);
		} else {
		return template.query(query, mapper );}
	
//		ResultSet set = getResult(query + ";");
//		String supplierTitle, date, rate, net, cases, agent, amountReceived, dealStr, fruit, qualityType, boxSizeType;
//		while (set.next()) {
//			supplierTitle = set.getString("supplierTitle");
//			if (supplierTitle == null || "".equals(supplierTitle)) {
//				continue;
//			}
//			date = set.getString("date");
//			rate = set.getString("supplierRate");
//			net = set.getString("net");
//			cases = set.getString("cases");
//			agent = set.getString("agent");
//			amountReceived = set.getString("amountReceived");
//			qualityType = set.getString("qualityType");
//			boxSizeType = set.getString("boxSizeType");
//			fruit = set.getString("fruit");
//			Long dealId = set.getLong("dealID");
//			String proprietor = getSupplierByName(supplierTitle).getProprietor();
//			String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
//			lines.add(new DSupplierTableLine("" + set.getLong("id"), supplierTitle.trim(), date, rate, net, cases,
//					agent, amountReceived, dealId + "", proprietor, amanat, fruit, qualityType, boxSizeType));
//		}
//		set.close();
//		return lines;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ISupplierDao#getSupplierDealEntry(int)
	 */
	@Override
	public DSupplierTableLine getSupplierDealEntry(Long id) throws SQLException {
		
		initTemplate();
		return template.query("SELECT * FROM supplierDeals WHERE id= ?",mapper, id).get(0);
//		String query = "SELECT * FROM supplierDeals WHERE id=" + id + ";";
//		ResultSet set = getResult(query);
//		String supplierTitle, date, rate, net, cases, agent, amountReceived, fruit, qualityType, boxSizeType;
//		if (set.next()) {
//			supplierTitle = set.getString("supplierTitle").trim();
//			date = set.getString("date");
//			rate = set.getString("supplierRate");
//			net = set.getString("net");
//			cases = set.getString("cases");
//			agent = set.getString("agent");
//			amountReceived = set.getString("amountReceived");
//			qualityType = set.getString("qualityType");
//			boxSizeType = set.getString("boxSizeType");
//			fruit = set.getString("fruit");
//			Long dealId = set.getLong("dealID");
//			String proprietor = getSupplierByName(supplierTitle).getProprietor();
//			String amanat = dtd.getSalesEntryLineByDealId(dealId).getAmanat();
//			set.close();
//			return new DSupplierTableLine("" + set.getLong("id"), supplierTitle, date, rate, net, cases, agent,
//					amountReceived, dealId + "", proprietor, amanat, fruit, qualityType, boxSizeType);
//		} else {
//			throw new NoSuchElementException("Supplier deal isn't found");
//		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ISupplierDao#getRowsNum(java.lang.String)
	 */
//	@Override
//	public int getRowsNum(String tablename) {
//		int rowsNum = 0;
//		try {
//			ResultSet result = getResult("SELECT COUNT(*) FROM " + tablename);
//			while (result.next()) {
//				rowsNum = result.getInt(1);
//			}
//			result.close();
//		} catch (SQLException e) {
//			System.out.print("sql exception in getRowsNum\n");
//		}
//
//		return rowsNum;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.ISupplierDao#saveSupplier(com.quickveggies.entities.
	 * Supplier)
	 */
	@Override
	public void saveSupplier(Supplier item) throws SQLException {
		initInsert();
		String firstName = item.getFirstName();
	int rows = template.query("SELECT COUNT(*) FROM suppliers1",SingleColumnRowMapper.newInstance(Integer.class)).get(0);
	String title = String.valueOf(rows).concat(" ").concat(firstName);
//		try {// check if supplier exists
//			this.getSupplierByName(title);
//			GeneralMethods.errorMsg("Saving failed - title already exists in database");
//		
//		} catch (NoSuchElementException e) {
//		}
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		
		
		args.put("title",item.getTitle());
		args.put("firstName", item.getFirstName());
		args.put("lastName", item.getLastName());
		args.put("company",item.getCompany());
		args.put("proprietor", item.getProprietor());
		args.put("mobile", item.getMobile());
		args.put("mobile2",item.getMobile2());
		args.put("email",item.getEmail());
		args.put("village", item.getVillage());
		args.put("po", item.getPo());
		args.put("tehsil",item.getTehsil());
		args.put("ac", item.getAc());
		args.put("bank", item.getBank());
		args.put("ifsc",item.getIfsc());
		args.put("photo",item.getImageStream());
		Long id = insert.executeAndReturnKey(args).longValue();
		ad.insertAuditRecord(new AuditLog(0l, userUtils.getCurrentUser(), null,
				"ADDED new entry for supplier :".concat(title), "Suppliers1", id));
		
		
//		String lastName = supplier.getLastName();
//		String company = supplier.getCompany();
//		String proprietor = supplier.getProprietor();
//		String mobile = supplier.getMobile();
//		String mobile2 = supplier.getMobile2();
//		String email = supplier.getEmail();
//		String village = supplier.getVillage();
//		String po = supplier.getPo();
//		String tehsil = supplier.getTehsil();
//		String ac = supplier.getAc();
//		String bank = supplier.getBank();
//		String ifsc = supplier.getIfsc();
//		String sql = "insert into suppliers1 (title,firstName,lastName,company,proprietor,mobile,mobile2,email,village,po,tehsil,ac,bank,ifsc, photo) "
//				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		PreparedStatement statement = dataSource.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//		statement.setString(1, title);
//		statement.setString(2, firstName);
//		statement.setString(3, lastName);
//		statement.setString(4, company);
//		statement.setString(5, proprietor);
//		statement.setString(6, mobile);
//		statement.setString(7, mobile2);
//		statement.setString(8, email);
//		statement.setString(9, village);
//		statement.setString(10, po);
//		statement.setString(11, tehsil);
//		statement.setString(12, ac);
//		statement.setString(13, bank);
//		statement.setString(14, ifsc);
//		statement.setBlob(15, supplier.getImageStream());
//		Long genId = (long) db.getGeneratedKey(statement);
//		statement.executeUpdate();
//		statement.close();
//		ad.insertAuditRecord(new AuditLog(0l, userUtils.getCurrentUser(), null,
//				"ADDED new entry for supplier :".concat(title), "Suppliers1", genId));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ISupplierDao#getSupplierById(int)
	 */
	@Override
	public Supplier getSupplierById(Long id) throws SQLException, NoSuchElementException {
		initTemplate();

		return template.query("select * from suppliers1 where id=?", mapper2, id).get(0);

		// ResultSet rs = getResult("select * from suppliers1 where id='" + id +
		// "';");
		// if (rs.next()) {
		// String title = rs.getString("title");
		// String firstName = rs.getString("firstName");
		// String lastName = rs.getString("lastName");
		// String company = rs.getString("company");
		// String proprietor = rs.getString("proprietor");
		// String mobile = rs.getString("mobile");
		// String mobile2 = rs.getString("mobile2");
		// String email = rs.getString("email");
		// String village = rs.getString("village");
		// String po = rs.getString("po");
		// String tehsil = rs.getString("tehsil");
		// String ac = rs.getString("ac");
		// String bank = rs.getString("bank");
		// String ifsc = rs.getString("ifsc");
		//
		// Supplier receivedSupplier = new Supplier(id, title, firstName,
		// lastName, company, proprietor, mobile,
		// mobile2, email, village, po, tehsil, ac, bank, ifsc);
		// Blob photo = rs.getBlob("photo");
		// if (photo != null) {
		// receivedSupplier.setImageStream(photo.getBinaryStream());
		// }
		// rs.close();
		// return receivedSupplier;
		// } else {
		// return null;
		// // throw new NoSuchElementException("No supplier user in database");
		// }
	}

//	private ResultSet getResult(String query) throws SQLException {
//		Statement statement = dataSource.getConnection().createStatement();
//		ResultSet resultSet = statement.executeQuery(query);
//		return resultSet;
//	}

	public Supplier getSupplierByName(String name) throws SQLException, NoSuchElementException {
		initTemplate();
	System.out.println(name);

		return template.query("select * from suppliers1 where title=?", mapper2, name).get(0);

//		ResultSet rs = getResult("select * from suppliers1 where title='" + name + "';");
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
//			String village = rs.getString("village");
//			String po = rs.getString("po");
//			String tehsil = rs.getString("tehsil");
//			String ac = rs.getString("ac");
//			String bank = rs.getString("bank");
//			String ifsc = rs.getString("ifsc");
//
//			Supplier receivedSupplier = new Supplier(id, title, firstName, lastName, company, proprietor, mobile,
//					mobile2, email, village, po, tehsil, ac, bank, ifsc);
//			Blob photo = null;
//			try {
//				photo = rs.getBlob("photo");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				// e.printStackTrace();
//			}
//			if (photo != null) {
//				receivedSupplier.setImageStream(photo.getBinaryStream());
//			}
//			rs.close();
//			return receivedSupplier;
//		} else {
//			throw new NoSuchElementException("No Supplier user in database");
//		}
	}

	@Override
	public List<DSupplierTableLine> getListBySupplierType(String title) {
		initTemplate();
		if ((title == null) || (title.trim().length() == 0)) {
			return template.query("select * from  supplierDeals", mapper);
		}
		// Map<String, Object> params = new HashMap<String, Object>();
		// params.put("title", "%" + title + "%");
		List<DSupplierTableLine> list = template.query("select * from  supplierDeals where suppliertitle like ? ",
				mapper, "%" + title.trim() + "%");
		return list;

	}

	@Override
	public List<Supplier> getListBySupplierTitle(String name) {
		initTemplate();
		if ((name == null) || (name.trim().length() == 0)) {
			return template.query("select * from suppliers1 ", mapper2);
		}
		// Map<String, Object> params = new HashMap<String, Object>();
		// params.put("title", "%" + title + "%");
		List<Supplier> list = template.query("select * from suppliers1 where title like ? ", mapper2,
				"%" + name.trim() + "%");
		return list;

	}

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);
		}
	}

}
