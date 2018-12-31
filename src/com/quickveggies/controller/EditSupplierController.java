package com.quickveggies.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.quickveggies.BeanUtils;
import com.quickveggies.dao.DatabaseClient;

import com.quickveggies.dao.SupplierDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EditSupplierController implements Initializable {

    @FXML
    private TextField title;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField company;

    @FXML
    private TextField proprietor;

    @FXML
    private TextField mobile;

    @FXML
    private TextField mobile2;

    @FXML
    private TextField email;

    @FXML
    private TextField village;

    @FXML
    private TextField po;

    @FXML
    private TextField tehsil;

    @FXML
    private TextField ac;

    @FXML
    private TextField bank;

    @FXML
    private TextField ifsc;

    @FXML
    private Button save;

    
    private Supplier supplierToEdit=null;
    
    
    private SupplierDao supplierDao;
    
    private DatabaseClient dbclient;
    
    public EditSupplierController(String supplierTitle){
    	//find the buyer in the database
    	try{
    	supplierToEdit=supplierDao.getSupplierByName(supplierTitle);  
    	}catch(SQLException e){System.out.print("sqlexception in EditSupplierController");}
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    	
    	supplierDao = BeanUtils.getBean(SupplierDao.class);
    	dbclient = BeanUtils.getBean(DatabaseClient.class);
    	title.setText(supplierToEdit.getTitle());
    	firstName.setText(supplierToEdit.getFirstName());
    	lastName.setText(supplierToEdit.getLastName());
    	mobile2.setText(supplierToEdit.getMobile2());
    	company.setText(supplierToEdit.getCompany());
    	proprietor.setText(supplierToEdit.getProprietor());
    	mobile.setText(supplierToEdit.getMobile());
    	email.setText(supplierToEdit.getEmail());
    	village.setText(supplierToEdit.getVillage());
    	po.setText(supplierToEdit.getPo());
    	tehsil.setText(supplierToEdit.getTehsil());
    	ac.setText(supplierToEdit.getAc());
    	bank.setText(supplierToEdit.getBank());
    	
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	dbclient.updateTableEntry("suppliers1", supplierToEdit.getId(), 
                    		new String[]{"title","firstName","lastName","company","proprietor","mobile","mobile2","email","village","po","tehsil","ac","bank"}, 
                    		new String[]{title.getText(),firstName.getText(),lastName.getText(),company.getText(),proprietor.getText(),mobile.getText(),
                    				mobile2.getText(),email.getText(),village.getText(),po.getText(),tehsil.getText(),ac.getText(),bank.getText()}
                            ,false);           
                     save.getScene().getWindow().hide();
            }
        });
    }
}
