package com.quickveggies;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanUtils {

	
	static ClassPathXmlApplicationContext context = 
			new ClassPathXmlApplicationContext("applicationContext.xml");

	
	
	public static <T> T getBean( Class<T> type) {

		return   context.getBean(type);
	}
}
