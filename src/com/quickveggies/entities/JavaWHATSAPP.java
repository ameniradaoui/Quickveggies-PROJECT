package com.quickveggies.entities;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class JavaWHATSAPP {
	
	private OptionsUtils option;
	
	  public void sendMessage(List<String> number, String message) throws Exception {
		    // TODO: Should have used a 3rd party library to make a JSON string from an object
		    String jsonPayload = new StringBuilder()
		      .append("{")
		      .append("\"number\":\"")
		      .append(number)
		      .append("\",")
		      .append("\"message\":\"")
		      .append(message)
		      .append("\"")
		      .append("}")
		      .toString();

		    URL url = new URL(option.WA_GATEWAY_URL);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    conn.setDoOutput(true);
		    conn.setRequestMethod("POST");
		    conn.setRequestProperty("X-WM-CLIENT-ID", option.CLIENT_ID);
		    conn.setRequestProperty("X-WM-CLIENT-SECRET", option.CLIENT_SECRET);
		    conn.setRequestProperty("Content-Type", "application/json");

		    OutputStream os = conn.getOutputStream();
		    os.write(jsonPayload.getBytes());
		    os.flush();
		    os.close();

		    int statusCode = conn.getResponseCode();
		    System.out.println("Response from WA Gateway: \n");
		    System.out.println("Status Code: " + statusCode);
		    BufferedReader br = new BufferedReader(new InputStreamReader(
		        (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()
		      ));
		    String output;
		    while ((output = br.readLine()) != null) {
		        System.out.println(output);
		    }
		    conn.disconnect();
		  }
}

