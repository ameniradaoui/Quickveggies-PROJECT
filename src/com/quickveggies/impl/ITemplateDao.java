package com.quickveggies.impl;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.quickveggies.entities.Template;

public interface ITemplateDao {

	Template getTemplate(String accountName) throws SQLException, NoSuchElementException;

	Template getTemplate();

	void saveTemplate(Template template);

	void updateTemplate(Template template);

}