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
	
	

	private ResultSetExtractor<Map<String, Object>> tag_options_mapper = new ResultSetExtractor<Map<String, Object>>() {

		@Override
		public Map<String, Object> extractData(ResultSet res) throws SQLException, DataAccessException {
			Map<String, Object> ret = new HashMap<String, Object>();
			while (res.next()) {
				ret.put(res.getString(1), res.getString(2));

			}
			return ret;
		}

	};
	
	
	@Override
	public Map<String, Object> Options() {

		initTemplate();
		return template.query("select * from  options ", tag_options_mapper);

		}

	@Override
	public void setTagOptions( Map<String, String> options) {

		initTemplate();
		template.update("delete from options ");

		for (String key : options.keySet()) {
			template.update("INSERT INTO options VALUES (?, ?);", key, options.get(key));

		}
	}
}
