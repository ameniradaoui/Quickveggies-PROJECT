package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.Template;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.ITemplateDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class TemplateDao implements ITemplateDao {
	

	 
	 

	
	private DataSource dataSource;
	
    private AuditDao auditDao;
    
    private UserUtils userDao;

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.ITemplateDao#getTemplate(java.lang.String)
	 */
    @Override
	public Template getTemplate(String accountName) throws SQLException, NoSuchElementException {
        PreparedStatement query = dataSource.getConnection().prepareStatement(""
                + "select * from templates where accountName=?;");
        query.setString(1, accountName);
        ResultSet set = query.executeQuery();
        if (set.next()) {
            return new Template(set.getString("accountName"), set.getInt("transIdCol"), set.getInt("dateCol"),
                    set.getInt("chqnoCol"), set.getInt("descriptionCol"), set.getInt("withdrawalCol"),
                    set.getInt("depositCol"), set.getInt("balanceCol"));
        } else {
            throw new NoSuchElementException();
        }
    }
    /* (non-Javadoc)
	 * @see com.quickveggies.dao.ITemplateDao#getTemplate()
	 */
    @Override
	public Template getTemplate() {
        Template template = null;
        return template;

    }
    
    

    public TemplateDao() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.ITemplateDao#saveTemplate(com.quickveggies.entities.Template)
	 */
	@Override
	public void saveTemplate(Template template) {
        String query = "Select  * from templates  where accountName=?";
        String sql = "Insert into templates (accountName,dateCol, chqnoCol ,descriptionCol ,withdrawalCol ,depositCol ,balanceCol, transIdCol) VALUES (?,?,?,?,?,?,?,?) ";
        Template t = template;
        try (PreparedStatement queryPs = dataSource.getConnection().prepareStatement(query);
                PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            queryPs.setString(1, t.getAccountName());
            ResultSet rs = queryPs.executeQuery();
            if (rs.next()) {
                System.out.println("Existing template found, updating...");
                updateTemplate(t);
                return;
            }
            ps.setString(1, t.getAccountName());
            ps.setInt(2, t.getDateCol());
            ps.setInt(3, t.getChqnoCol());
            ps.setInt(4, t.getDescriptionCol());
            ps.setInt(5, t.getWithdrawalCol());
            ps.setInt(6, t.getDepositCol());
            ps.setInt(7, t.getBalanceCol());
            ps.setInt(8, t.getTransIdCol());
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null,
                    "ADDED Template for account:".concat(template.getAccountName()), null, 0));
            //System.out.println("ADDED a new template");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	/* (non-Javadoc)
	 * @see com.quickveggies.dao.ITemplateDao#updateTemplate(com.quickveggies.entities.Template)
	 */
    @Override
	public void updateTemplate(Template template) {
        String sql = "UPDATE templates set dateCol= ? , chqnoCol= ?  ,descriptionCol = ? ,withdrawalCol = ? ,depositCol = ? ,balanceCol= ? WHERE accountName = ?";
        Template t = template;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setInt(1, t.getDateCol());
            ps.setInt(2, t.getChqnoCol());
            ps.setInt(3, t.getDescriptionCol());
            ps.setInt(4, t.getWithdrawalCol());
            ps.setInt(5, t.getDepositCol());
            ps.setInt(6, t.getBalanceCol());
            ps.setString(7, t.getAccountName());
            ps.executeUpdate();
            auditDao.insertAuditRecord(new AuditLog(0, userDao.getCurrentUser(), null,
                    "UPDATED Template for account:".concat(template.getAccountName()), null, 0));

            System.out.println("Template updated");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



}
