package com.quickveggies.dao;

import static com.quickveggies.entities.Buyer.COLD_STORE_BUYER_TITLE;
import static com.quickveggies.entities.Buyer.GODOWN_BUYER_TITLE;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.AccountEntryPayment;
import com.quickveggies.entities.AuditLog;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.Journal;
import com.quickveggies.impl.IAuditDao;
import com.quickveggies.impl.IUserUtils;


@Component
public class JournalDao implements IJournalImp {
	

	
	private JdbcTemplate template;
	
	
	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);
			
		}
	}
	
	
	private SimpleJdbcInsert insert;
	private void initInsert() {
		if (insert == null) {
			createInsert();
			
		}
	}
	
	private void createInsert() {
		insert = new SimpleJdbcInsert(dataSource).withTableName("journal").usingGeneratedKeyColumns("journalno");
	}

	
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
		createInsert();
		
	}
	

	@Autowired
	 private DataSource dataSource;
	
	 
	 public DataSource getDataSource() {
		return dataSource;
	}

	 private static RowMapper<Journal> mapper = new RowMapper<Journal>() {
			@Override
			public Journal mapRow(ResultSet data, int index) throws SQLException {
				Journal item = new Journal();
				item.setJournalNo(data.getLong("journalno"));
				item.setJournalDate(data.getString("journaldate"));
				item.setAccount(data.getString("account"));
				item.setDebits(data.getString("debits"));
				item.setCredits(data.getString("credit"));
				item.setDescription(data.getString("description"));
				item.setName(data.getString("name"));
				item.setMemo(data.getString("memo"));
				item.setAttachement(data.getBinaryStream("attachements"));
						
				return item;
			}
		};
	
	

	/* (non-Javadoc)
	 * @see com.quickveggies.dao.IAuditDao#getAuditRecords()
	 */
	@Override
	public List<Journal> getJournals() {
		
		initTemplate();
		LocalDate date = LocalDate.now();
	     return  template.query("SELECT * FROM journal where journalDate=? ", mapper , date);
	     
	    
	    }
	
	@Override
	public List<Journal> getJournalsTable() {
		
		initTemplate();
		LocalDate date = LocalDate.now();
	     return  template.query("SELECT account,debits,credit,description,name FROM journal where journalDate=? ", mapper , date);
	     
	    
	    }
	
	@Autowired
	 private static  UserUtils userUtils;
	@Autowired
	 private static IJournalImp journalDao;
	 
	 public JournalDao() {
		super();
		// TODO Auto-generated constructor stub
	}

	 @Override
	public Long insertJournal(Journal item) throws IOException  {
		initInsert();
		
		Map<String, Object> args = new HashMap<String, Object>();
		// args.put("id", item.getId());
		args.put("journaldate", item.getJournalDate());
		args.put("account", item.getAccount());
		args.put("debits", item.getDebits());
		args.put("credit", item.getCredits());
		args.put("description", item.getDescription() );
		args.put("name", item.getName());
		args.put("memo", item.getMemo());
//		Blob blob = item.getBlob("logo");
//		if (blob != null) {
//			args.put("attachements", blob);
//		}
	byte[] bytes = IOUtils.toByteArray(item.getAttachement());
	args.put("attachements", bytes);
		
		Long id = insert.executeAndReturnKey(args).longValue();
        return id;
		

	    }

	 
	 @Override
		public void updateJournalInfo(Journal item, String name) {

			template.update("UPDATE journal SET journaldate=?,  account=?, debits=? , credit=? , description=? ,name = ? , memo=? , attachements =?  WHERE name = ?", item.getJournalDate(), 
					item.getAccount(), item.getDebits() , item.getCredits() , item.getDescription()  , item.getName(), item.getMemo() , item.getAttachement() ,name  );
		
		}

	
	 @Override
		public void deleteJournal(Long id) {

			String SQL = "DELETE FROM journal WHERE journalNo = ?";

			try {
				template.update(SQL, id);
				

			} catch (RuntimeException runtimeException) {

				System.err.println(runtimeException);
				throw runtimeException;
			}

		
		}

	


}
