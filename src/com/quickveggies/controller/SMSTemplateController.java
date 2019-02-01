package com.quickveggies.controller;


import java.awt.Color;

import java.net.URL;
import java.util.ResourceBundle;

import com.quickveggies.UserGlobalParameters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;



public class SMSTemplateController implements Initializable{
	
	@FXML
	private Button btnEditTemplate;
	
	@FXML
	private Button btnSaveTemplate;
	
	@FXML
	private Button btnBuyer;
	@FXML
	private Button btnSupplier;
	@FXML
	private Button btnReminder;

	@FXML
	private TextArea taSMSTemplate;
	
	DropShadow shadow = new DropShadow();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		taSMSTemplate.setEditable(false);
		taSMSTemplate.setText(UserGlobalParameters.GET_SMS_TEMPLATE());
		taSMSTemplate.setWrapText(true);
		btnEditTemplate.setOnAction((event) -> {
			taSMSTemplate.setEditable(true);
			btnEditTemplate.setDisable(true);
			btnSaveTemplate.setDisable(false);
		});
		btnSaveTemplate.setOnAction((event) -> {
			btnEditTemplate.setDisable(false);
			UserGlobalParameters.SET_SMS_TEMPLATE(taSMSTemplate.getText(), true);
			btnSaveTemplate.setDisable(true);
			taSMSTemplate.setEditable(false);
		});
		
	
	
		btnBuyer.addEventHandler(MouseEvent.MOUSE_ENTERED,
			        new EventHandler<MouseEvent>() {
			          @Override
			          public void handle(MouseEvent e) {
			        	  btnBuyer.setEffect(shadow);
			          }
			        });
	
		btnBuyer.addEventHandler(MouseEvent.MOUSE_EXITED,
			        new EventHandler<MouseEvent>() {
			          @Override
			          public void handle(MouseEvent e) {
			        	  btnBuyer.setEffect(null);
			          }
			        });
		btnSupplier.addEventHandler(MouseEvent.MOUSE_ENTERED,
		        new EventHandler<MouseEvent>() {
		          @Override
		          public void handle(MouseEvent e) {
		        	  btnSupplier.setEffect(shadow);
		          }
		        });

		btnSupplier.addEventHandler(MouseEvent.MOUSE_EXITED,
		        new EventHandler<MouseEvent>() {
		          @Override
		          public void handle(MouseEvent e) {
		        	  btnSupplier.setEffect(null);
		          }
		        });
		btnReminder.addEventHandler(MouseEvent.MOUSE_ENTERED,
		        new EventHandler<MouseEvent>() {
		          @Override
		          public void handle(MouseEvent e) {
		        	  btnReminder.setEffect(shadow);
		          }
		        });

		btnReminder.addEventHandler(MouseEvent.MOUSE_EXITED,
		        new EventHandler<MouseEvent>() {
		          @Override
		          public void handle(MouseEvent e) {
		        	  btnReminder.setEffect(null);
		          }
		        });
		
		

	}

}
