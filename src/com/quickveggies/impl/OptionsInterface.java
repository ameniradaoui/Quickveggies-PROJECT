package com.quickveggies.impl;

import java.util.Map;

public interface OptionsInterface {

	Map<String, Object> Options();


	


	void setTagOptions(Map<String, String> options);





	Map<String, Object> getConfigSms();





	Map<String, Object> getConfigEmail();





	Map<String, Object> getConfigWhatsapp();

}
