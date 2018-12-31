package com.quickveggies.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.dao.ExpenseDao;
import com.quickveggies.dao.UserUtils;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ExpenseInfoAddController implements Initializable{

	@FXML
	private Button btnSave;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private ComboBox<String> cboChargeTypes;
	
	@FXML
	private TextField txtAmount;
	
	
	private ExpenseDao expenseDao;
	
    private String[] chargeTypes = new String[] {"%","@"};

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		expenseDao = BeanUtils.getBean(ExpenseDao.class);
		
		cboChargeTypes.setItems(FXCollections.observableArrayList(chargeTypes));
		cboChargeTypes.setValue("%");

		btnSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String name = txtName.getText();
				if (name == null || name.trim().isEmpty()) {
					GeneralMethods.errorMsg("Expense name cannot be null");
				}
				String type  = cboChargeTypes.getValue();
				String defAmount = txtAmount.getText();
				if (defAmount == null || defAmount.trim().isEmpty()) {
					defAmount = null;
				}
				expenseDao.addExpenseInfo(name, type, defAmount);
				btnSave.getScene().getWindow().hide();
			}
		});
	}

}
