package com.quickveggies.impl;

import java.util.Map;

public interface OptionsInterface {

	Map<String, String> Options();


	


	void setTagOptions(Map<String, String> options);





	Map<String, String> getConfigSms();





	Map<String, String> getConfigEmail();





	Map<String, String> getConfigWhatsapp();

}
