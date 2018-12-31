package com.quickveggies.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Company;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.ICompanyDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class CompanyDao implements ICompanyDao {

	
	private DataSource dataSource;
	
	private BuyerDao bd;
	
	private SupplierDao supplierDao;
	
	private DSalesTableDao dSalesDao;
	
	private AuditDao auditDao;

	
	private UserUtils userDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.quickveggies.dao.ICompanyDao#addCompany(com.quickveggies.entities.
	 * Company)
	 */
	@Override
	public void addCompany(Company company) {
		String sql = "Select count(*) from companyInfo;";
		try (ResultSet rs = userDao.getResult(sql)) {
			if (rs.next()) {
				if (rs.getInt(1) < 1) {
					addOrUpdateCompanyInfo(company, true);
					auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null,
							"ADDED company:".concat(company.getName()), null, 0));

				} else {
					addOrUpdateCompanyInfo(company, false);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
		auditDao.insertAuditRecord(
				new AuditLog(0, userDao.getCurrentUser(), null, "UPDATED company:".concat(company.getName()), null, 0));

	}

	private static final String INSERT_COMPANY_QRY = "INSERT INTO companyInfo ("
			+ "name ,   address ,   website ,   phone ,  email ,  industryType ,  password ,  logo )  VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_COMPANY_QRY = "UPDATE companyInfo SET "
			+ "name=? ,   address =? ,   website = ?,   phone = ?,  email = ?,  industryType = ?,  password = ?,  logo = ? WHERE  name = ?";

	private static final String UPDATE_NOLOGO_COMPANY_QRY = "UPDATE companyInfo SET "
			+ "name=? ,   address =? ,   website = ?,   phone = ?,  email = ?,  industryType = ?,  password = ?   WHERE  name = ?";

	// ## changed by ss on 23-Dec-2017(all queries below are not supported by
	// postgres so changed.)

	private void addOrUpdateCompanyInfo(Company company, boolean isNew) {
		String sql = "";
		if (isNew) {
			sql = INSERT_COMPANY_QRY;
		} else {
			if (company.getLogo() != null) {
				sql = UPDATE_COMPANY_QRY;
			} else {
				sql = UPDATE_NOLOGO_COMPANY_QRY;
			}
		}
		try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
			ps.setString(1, company.getName());
			ps.setString(2, company.getAddress());
			ps.setString(3, company.getWebsite());
			ps.setString(4, company.getPhone());
			ps.setString(5, company.getEmail());
			ps.setString(6, company.getIndustryType());
			ps.setString(7, company.getPassword());
			if (company.getLogo() != null || isNew) {
				ps.setBlob(8, company.getLogo());
				if (!isNew) {
					ps.setString(9, company.getName());
				}
			} else if (company.getLogo() == null && !isNew) {
				ps.setString(8, company.getName());
			}
			ps.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.quickveggies.dao.ICompanyDao#getCompany()
	 */
	@Override
	public Company getCompany() {
		String sql = "Select * from companyInfo;";
		Company c = null;
		try (ResultSet rs = userDao.getResult(sql)) {
			if (rs.next()) {
				c = new Company();
				c.setAddress(rs.getString("address"));
				c.setEmail(rs.getString("email"));
				c.setIndustryType(rs.getString("industryType"));
				Blob blob = rs.getBlob("logo");
				if (blob != null) {
					c.setLogo(blob.getBinaryStream());
				}
				c.setName(rs.getString("name"));
				c.setPassword(rs.getString("password"));
				c.setPhone(rs.getString("phone"));
				c.setWebsite(rs.getString("website"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

}
