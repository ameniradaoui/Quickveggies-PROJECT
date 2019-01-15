package com.quickveggies.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.table.TableColumn;
import javax.swing.text.TableView;

import com.quickveggies.entities.PartyProfile;
import com.quickveggies.misc.FruitButtonEventHandler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;



public class SettingsController implements Initializable {
	
	
	
	@FXML
	private Button btnAddUser;
	
	@FXML
	private TableView table;
	
	
	@FXML
	private TableColumn name;
	@FXML
	private TableColumn type;
	@FXML
	private TableColumn email;
	
	@FXML
	private Pane settingsPane;
	
	@FXML
	private ScrollPane paneProducts; 
	
	@FXML
	private AnchorPane ancPaneProducts;
	
	private SessionDataController session = SessionDataController.getInstance();
	
	@FXML
	private Pane fruitSettingsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	
    	VBox content = new VBox();
		content.setPrefHeight(paneProducts.getHeight());
		content.setPrefWidth(paneProducts.getWidth());
		
		
		paneProducts.setContent(content);
    	session.setSettingPagePane(fruitSettingsPane);
    	//System.out.println(fruitSettingsPane.getChildren().indexOf(paneProducts));
    
    	//btnAddUser.setOnAction(new FruitButtonEventHandler("/fxml/register.fxml", "New User ", fruitSettingsPane));

    }
}
