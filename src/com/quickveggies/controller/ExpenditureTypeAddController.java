package com.quickveggies.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.dao.ExpenditureDao;
import com.quickveggies.dao.UserUtils;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ExpenditureTypeAddController implements Initializable {

    @FXML
    private TableView<String> tvExpenditureType;

    @FXML
    private TextField txtFruitType;

    @FXML
    private TextField txtExpType;

    @FXML
    private Button btnAddNewType;

    @FXML
    private Button btnSave;

    private ObservableList<String> expTypes = FXCollections.observableArrayList();
    
    
	private ExpenditureDao ed ;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	ed = BeanUtils.getBean(ExpenditureDao.class);
    	
        tvExpenditureType.setEditable(true);

        tvExpenditureType.setItems(expTypes);
        TableColumn<String, String> xpTypeCol = (TableColumn<String, String>) tvExpenditureType.getColumns().get(0);
        tvExpenditureType.getColumns().retainAll(new TableColumn[] {xpTypeCol});

        xpTypeCol.setCellValueFactory((CellDataFeatures<String, String> param)
                -> new SimpleStringProperty(param.getValue()));

        xpTypeCol.setEditable(true);

        tvExpenditureType.setItems(expTypes);

        btnAddNewType.setOnAction((ActionEvent event) -> {
            String expType = txtExpType.getText();
            if (expType == null || expType.trim().isEmpty()) {
                GeneralMethods.errorMsg("Expenditure type cannot be empty");
                return;
            }
            expTypes.add(expType);
            tvExpenditureType.refresh();
            txtExpType.clear();
        });

        btnSave.setOnAction((ActionEvent event) -> {
            
            for (String str : expTypes) {
            	ed.addExpenditureType(str);
            }
            GeneralMethods.msg("Save process completed, please see the logs if there are any unreported errors!");
            tvExpenditureType.getItems().clear();
            btnSave.getScene().getWindow().hide();
        });

    }

}
