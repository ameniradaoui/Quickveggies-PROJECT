package com.quickveggies.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.Session;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.PropertyAccessorFactory;

import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.dao.BuyerDao;
import com.quickveggies.dao.JournalDao;
import com.quickveggies.dao.MoneyPaidRecdDao;
import com.quickveggies.dao.OptionsDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.Journal;
import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.entities.OptionsUtils;
import com.quickveggies.entities.User;
import com.quickveggies.misc.EditingCell;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.layout.Pane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn.CellEditEvent;

public class SmsController implements Initializable {

	

	@FXML
	private TextField clientId;
	@FXML
	private TextField clientSecret;
	@FXML
	private TextField phonenumber;
	@FXML
	private TextField requestUrl;
	@FXML
	private TextField authUrl;
	@FXML
	private Button create;	
	
	Map<String, String> map=new HashMap<>();


	private ObservableList<Journal> data = FXCollections.observableArrayList();
	
	
	private OptionsDao optionsDao;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		optionsDao = BeanUtils.getBean(OptionsDao.class);
		create.setOnAction((ActionEvent event) -> {
	           
			
				saveMprObject();
			
		
				create.getScene().getWindow().hide();
    });

		 
		 
	}



	private void saveMprObject() {
		
		map.put(OptionsUtils.SMS_CLIENT_ID, clientId.getText());
		map.put(OptionsUtils.SMS_CLIENT_SECRET, clientSecret.getText());
		map.put(OptionsUtils.SMS_OAUTH_URL, authUrl.getText());
		map.put(OptionsUtils.SMS_PHONE_NUMBER, phonenumber.getText());
		map.put(OptionsUtils.SMS_REQUEST_URL, requestUrl.getText());
		optionsDao.setTagOptions(map);
		
		
		
	}

}
