package com.quickveggies;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanUtils {

	
	static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");;
			

	
	
	public static <T> T getBean( Class<T> type) {
		
		return   context.getBean(type);
	}
}
