package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Charge;
import com.quickveggies.entities.Company;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.ICompanyDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;

@Component
public class CompanyDao implements ICompanyDao {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private BuyerDao bd;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private DSalesTableDao dSalesDao;
	@Autowired
	private AuditDao auditDao;

	@Autowired
	private UserUtils userDao;

	private static final String INSERT_COMPANY_QRY = "INSERT INTO companyInfo ("
			+ "name ,   address ,   website ,   phone ,  email ,  industryType ,  password ,  logo )  VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_COMPANY_QRY = "UPDATE companyInfo SET "
			+ "name=? ,   address =? ,   website = ?,   phone = ?,  email = ?,  industryType = ?,  password = ?,  logo = ? WHERE  name = ?";

	private static final String UPDATE_NOLOGO_COMPANY_QRY = "UPDATE companyInfo SET "
			+ "name=? ,   address =? ,   website = ?,   phone = ?,  email = ?,  industryType = ?,  password = ?   WHERE  name = ?";

	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insert == null) {
			createInsert();

		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("companyInfo").usingGeneratedKeyColumns("id");
	}

	private static RowMapper<Company> Mapper = new RowMapper<Company>() {
		@Override
		public Company mapRow(ResultSet data, int index) throws SQLException {
			Company item = new Company();
			item.setId(data.getLong("id"));
			item.setName(data.getString("name"));
			item.setAddress(data.getString("address"));
			item.setWebsite(data.getString("website"));
			item.setPhone(data.getString("phone"));
			item.setEmail(data.getString("email"));
			item.setIndustryType(data.getString("industrytype"));
			item.setPassword(data.getString("password"));

			Blob blob = data.getBlob("logo");
			if (blob != null) {
				item.setLogo(blob.getBinaryStream());
			}

			return item;
		}
	};

	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();

	}

	private JdbcTemplate template;

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.ICompanyDao#addCompany(com.quickveggies.entities.
	 * Company)
	 */
	@Override
	public Long addCompany(Company company) {

		initTemplate();
		Long result = template.query("Select count(*) from companyInfo", SingleColumnRowMapper.newInstance(Long.class))
				.get(0);

		if (result < 1) {
			addOrUpdateCompanyInfo(company, true);
			auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
					"ADDED company:".concat(company.getName()), null, 0l));
		}

		return result;
		// String sql = "Select count(*) from companyInfo;";
		// try (ResultSet rs = getResult(sql)) {
		// if (rs.next()) {
		// if (rs.getInt(1) < 1) {
		// addOrUpdateCompanyInfo(company, true);
		// auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(),
		// null,
		// "ADDED company:".concat(company.getName()), null, 0l));
		//
		// } else {
		// addOrUpdateCompanyInfo(company, false);
		// }
		// }
		// rs.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	// private ResultSet getResult(String query) throws SQLException {
	// Statement statement = dataSource.getConnection().createStatement();
	// ResultSet resultSet = statement.executeQuery(query);
	//
	// return resultSet;
	// }
	public CompanyDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.ICompanyDao#updateCompany(com.quickveggies.entities.
	 * Company)
	 */
	@Override
	public void updateCompany(Company company) {
		addOrUpdateCompanyInfo(company, false);
		auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
				"UPDATED company:".concat(company.getName()), null, 0l));

	}

	// ## changed by ss on 23-Dec-2017(all queries below are not supported by
	// postgres so changed.)

	private void addOrUpdateCompanyInfo(Company item, boolean isNew) {
		initTemplate();
		initInsert();
		String sql = "";
//		if (isNew) {
//			sql = INSERT_COMPANY_QRY;
//		} else {
//			if (item.getLogo() != null) {
//				sql = UPDATE_COMPANY_QRY;
//			} else {
//				sql = UPDATE_NOLOGO_COMPANY_QRY;
//			}
//		}
		
		if (isNew){
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("name", item.getName());
		args.put("address", item.getAddress());
		args.put("website", item.getWebsite());
		args.put("phone", item.getPhone());
		args.put("email", item.getEmail());
		args.put("industrytype", item.getIndustryType());
		args.put("password", item.getPassword());
		args.put("logo", item.getLogo());
		
		
		if (item.getLogo() != null || isNew) {
			args.put("name", item.getName());
			if (!isNew) {
				args.put("name", item.getName());
			}
		} else if (item.getLogo() == null && !isNew) {
			args.put("name", item.getName());
		}
		

		
		else {
			if (item.getLogo() != null) {
				sql = UPDATE_COMPANY_QRY;
				
				template.update(sql, item.getName() ,item.getAddress() ,item.getWebsite(),item.getPhone() ,  item.getEmail() ,  item.getIndustryType() ,  item.getPassword(),  item.getLogo() , item.getName());
			} else {
				sql = UPDATE_NOLOGO_COMPANY_QRY;
				template.update(sql,  item.getName() ,item.getAddress() ,item.getWebsite(),item.getPhone() ,  item.getEmail() ,  item.getIndustryType() ,  item.getPassword(), item.getName());
			}
		}}
		
//		try (Connection connection = dataSource.getConnection();
//				PreparedStatement ps = connection.prepareStatement(sql)) {
//			ps.setString(1, company.getName());
//			ps.setString(2, company.getAddress());
//			ps.setString(3, company.getWebsite());
//			ps.setString(4, company.getPhone());
//			ps.setString(5, company.getEmail());
//			ps.setString(6, company.getIndustryType());
//			ps.setString(7, company.getPassword());
//			if (company.getLogo() != null || isNew) {
//				ps.setBlob(8, company.getLogo());
//				if (!isNew) {
//					ps.setString(9, company.getName());
//				}
//			} else if (company.getLogo() == null && !isNew) {
//				ps.setString(8, company.getName());
//			}
//			ps.executeUpdate();
//			ps.close();
//			connection.close();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ICompanyDao#getCompany()
	 */
	@Override
	public Company getCompany() {

		initTemplate();

		List<Company> list = template.query("Select * from companyInfo;", Mapper);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}

		// String sql = "Select * from companyInfo;";
		// Company c = null;
		// try (ResultSet rs = getResult(sql)) {
		// if (rs.next()) {
		// c = new Company();
		// c.setAddress(rs.getString("address"));
		// c.setEmail(rs.getString("email"));
		// c.setIndustryType(rs.getString("industryType"));
		// Blob blob = rs.getBlob("logo");
		// if (blob != null) {
		// c.setLogo(blob.getBinaryStream());
		// }
		// c.setName(rs.getString("name"));
		// c.setPassword(rs.getString("password"));
		// c.setPhone(rs.getString("phone"));
		// c.setWebsite(rs.getString("website"));
		// }
		// rs.close();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		// return c;
	}

}
