package com.quickveggies.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.Pane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

public class AccountController implements Initializable{

	@FXML
	private TextArea textl;
	@FXML
	private ComboBox accountType;
	@FXML
	private ComboBox details;
	@FXML
	private TextField namefield;
	@FXML
	private TextField description;
	@FXML
	private TextField parentAccount;
	@FXML
	private TextField txtbalance;
	@FXML
	private DatePicker date;
	@FXML
	private CheckBox chkbox;
	@FXML
	private Button create;
	@FXML
	private Button cancel;
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		textl.setText("Use a CASH ON HAND account to track cash "
				+ "your company keeps for occasional expenses ,"
				+ " also called petty cash. To track cash from "
				+ "sales that have not been deposited yet, "
				+ "Use a pre-created account called UNDEPOSITED"
				+ " FUNDS , instead. ");
		
		accountType.getItems().addAll("Bank" , "Account receivable(A/R" , "other Current Assets" , "Fixed Assets" , "Other Expense" , "Account Payable (A/P)" , "Credit Card" , "Other Current Liabilities" , "Long Term Liabilities" , "Long Term Liabilities" , "Equity" , "Income" , "Cost of Goods sold" , "Expenses");
		accountType.setValue("Bank");
		
		details.getItems().addAll("Cash on hand");
		details.setValue("Cash on hand");
		
		
		
	}

}
