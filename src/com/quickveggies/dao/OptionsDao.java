package com.quickveggies.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.quickveggies.entities.Journal;
import com.quickveggies.entities.OptionsUtils;
import com.quickveggies.impl.OptionsInterface;


@Component
public class OptionsDao implements OptionsInterface  {
	
	@Autowired
	private DataSource dataSource;

	private void initTemplate() {
		if (template == null) {
			template = new JdbcTemplate(dataSource);

		}
	}
	public void setDataSource(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	
	}
	private JdbcTemplate template;
	
	

	private ResultSetExtractor<Map<String, String>> options_mapper = new ResultSetExtractor<Map<String, String>>() {

		@Override
		public Map<String, String> extractData(ResultSet res) throws SQLException, DataAccessException {
			Map<String, String> ret = new HashMap<String, String>();
			while (res.next()) {
				ret.put(res.getString(1), res.getString(2));

			}
			return ret;
		}

	};
	
	
	@Override
	public Map<String, String> Options() {

		initTemplate();
		return template.query("select * from  options ", options_mapper);

		}

	@Override
	public void setTagOptions( Map<String, String> options) {

		initTemplate();
		

		for (String key : options.keySet()) {
			template.update("delete from options where key = ?" , key);
			template.update("INSERT INTO options VALUES (?, ?);", key, options.get(key));

		}
	}
	
	@Override
	public Map<String, String> getConfigSms(){
		
	initTemplate();
	return template.query("Select * from options where key =? or key= ? or key =? or key=? or key=?" , options_mapper , OptionsUtils.SMS_CLIENT_ID , 
			OptionsUtils.SMS_CLIENT_SECRET , OptionsUtils.SMS_OAUTH_URL , OptionsUtils.SMS_PHONE_NUMBER , OptionsUtils.SMS_REQUEST_URL);
	
	}
	
	@Override
	public Map<String, String> getConfigEmail(){
		
	initTemplate();
	return template.query("Select * from options where key =? or key= ? or key =? or key=?" , options_mapper , OptionsUtils.SMTP_HOST , 
			OptionsUtils.SMTP_LOGIN , OptionsUtils.SMTP_PASSWORD , OptionsUtils.SMTP_PORT);
	
	}
	
	@Override
	public Map<String, String> getConfigWhatsapp(){
		
	initTemplate();
	return template.query("Select * from options where key =? or key= ? or key =? or key=? " , options_mapper , OptionsUtils.CLIENT_ID , 
			OptionsUtils.CLIENT_SECRET , OptionsUtils.INSTANCE_ID , OptionsUtils.WA_GATEWAY_URL );
	
	}
}
