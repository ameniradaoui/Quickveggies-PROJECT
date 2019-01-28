package com.quickveggies.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.StorageBuyerDeal;
import com.quickveggies.entities.Template;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IBuyerDao;
import com.quickveggies.impl.IDsalesTableDao;
import com.quickveggies.impl.ISupplierDao;
import com.quickveggies.impl.ITemplateDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class TemplateDao implements ITemplateDao {
	

	 
	 

	@Autowired
	private DataSource dataSource;
	@Autowired
    private AuditDao auditDao;
	@Autowired
    private UserUtils userDao;

    /* (non-Javadoc)
	 * @see com.quickveggies.dao.ITemplateDao#getTemplate(java.lang.String)
	 */
	private SimpleJdbcInsert insert;

	private void initInsert() {
		if (insert == null) {
			createInsert();

		}
	}

	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("templates").usingGeneratedKeyColumns("transIdCol");
	}
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
	
	private static RowMapper<Template> mapper = new RowMapper<Template>() {
		@Override
		public Template mapRow(ResultSet data, int index) throws SQLException {
			Template item = new Template();
			item.setAccountName(data.getString("accountName"));
			item.setTransIdCol(data.getInt("transIdCol"));
			item.setDateCol(data.getInt("dateCol"));
			item.setChqnoCol(data.getInt("chqnoCol"));
			item.setDescriptionCol(data.getInt("descriptionCol"));
			item.setWithdrawalCol(data.getInt("withdrawalCol"));
			item.setDepositCol(data.getInt("depositCol"));
			item.setBalanceCol(data.getInt("balanceCol"));

			return item;
		}
	};
    @Override
	public Template getTemplate(String accountName) throws SQLException, NoSuchElementException {
    	initTemplate();
    	return template.query("select * from templates where accountName=?", mapper ,accountName ).get(0);
    	
//    	 Connection connection = dataSource.getConnection();
//        PreparedStatement query = connection.prepareStatement(""
//                + "select * from templates where accountName=?;");
//        query.setString(1, accountName);
//        ResultSet set = query.executeQuery();
//        if (set.next()) {
//        	set.close();
//            return new Template(set.getString("accountName"), set.getInt("transIdCol"), set.getInt("dateCol"),
//                    set.getInt("chqnoCol"), set.getInt("descriptionCol"), set.getInt("withdrawalCol"),
//                    set.getInt("depositCol"), set.getInt("balanceCol"));
//        } else {
//            throw new NoSuchElementException();
//        }
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
	public void saveTemplate(Template item) {
		initInsert();
		initTemplate();
        String query = "Select  * from templates  where accountName=?";
        
        template.query(query, mapper , item.getAccountName());
        
      
        Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("accountName", item.getAccountName());
		args.put("dateCol", item.getDateCol());
		args.put("chqnoCol",item.getChqnoCol());
		args.put("descriptionCol", item.getDescriptionCol());
		args.put("withdrawalCol", item.getWithdrawalCol());
		args.put("depositCol",item.getDepositCol());
		args.put("balanceCol", item.getBalanceCol());
		args.put("transIdCol", item.getTransIdCol());
		
		 auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
	                "ADDED Template for account:".concat(item.getAccountName()), null, 0l));
		
//        String sql = "Insert into templates (accountName,dateCol, chqnoCol ,descriptionCol ,withdrawalCol ,depositCol ,balanceCol, transIdCol) VALUES (?,?,?,?,?,?,?,?) ";
//        Template t = template;
//        try ( Connection connection = dataSource.getConnection();
//        		PreparedStatement queryPs = dataSource.getConnection().prepareStatement(query);
//                PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
//            queryPs.setString(1, t.getAccountName());
//            ResultSet rs = queryPs.executeQuery();
//            if (rs.next()) {
//                System.out.println("Existing template found, updating...");
//                updateTemplate(t);
//                return;
//            }
//            ps.setString(1, t.getAccountName());
//            ps.setInt(2, t.getDateCol());
//            ps.setInt(3, t.getChqnoCol());
//            ps.setInt(4, t.getDescriptionCol());
//            ps.setInt(5, t.getWithdrawalCol());
//            ps.setInt(6, t.getDepositCol());
//            ps.setInt(7, t.getBalanceCol());
//            ps.setInt(8, t.getTransIdCol());
//            ps.executeUpdate();
//            ps.close();
//            connection.close();
//            
//            auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
//                    "ADDED Template for account:".concat(template.getAccountName()), null, 0l));
//         
//            //System.out.println("ADDED a new template");
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
    }

    public DataSource getDataSource() {
		return dataSource;
	}

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.ITemplateDao#updateTemplate(com.quickveggies.entities.Template)
	 */
    @Override
	public void updateTemplate(Template item) {
    	initTemplate();
    	template.update("UPDATE templates set dateCol= ? , chqnoCol= ?  ,descriptionCol = ? ,withdrawalCol = ? ,depositCol = ? ,balanceCol= ? WHERE accountName = ?",
    			item.getDateCol(),item.getChqnoCol(), item.getDescriptionCol() , item.getWithdrawalCol()
    			, item.getDepositCol() , item.getBalanceCol(), item.getAccountName());
    	  auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
                  "UPDATED Template for account:".concat(item.getAccountName()), null, 0l));
    	  
//        String sql = "UPDATE templates set dateCol= ? , chqnoCol= ?  ,descriptionCol = ? ,withdrawalCol = ? ,depositCol = ? ,balanceCol= ? WHERE accountName = ?";
//        Template t = template;
//        try ( Connection connection = dataSource.getConnection();
//        		PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setInt(1, t.getDateCol());
//            ps.setInt(2, t.getChqnoCol());
//            ps.setInt(3, t.getDescriptionCol());
//            ps.setInt(4, t.getWithdrawalCol());
//            ps.setInt(5, t.getDepositCol());
//            ps.setInt(6, t.getBalanceCol());
//            ps.setString(7, t.getAccountName());
//            ps.executeUpdate();
//            ps.close();
//            connection.close(); 
//            auditDao.insertAuditRecord(new AuditLog(0l, userDao.getCurrentUser(), null,
//                    "UPDATED Template for account:".concat(template.getAccountName()), null, 0l));
//           
//
//            System.out.println("Template updated");
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
    }



}
