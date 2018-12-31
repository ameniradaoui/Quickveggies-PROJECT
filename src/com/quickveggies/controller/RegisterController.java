package com.quickveggies.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.quickveggies.BeanUtils;
import com.quickveggies.CosmeticStyles;
import com.quickveggies.GeneralMethods;
import com.quickveggies.Main;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.User;
import com.quickveggies.misc.CryptDataHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController implements Initializable {

	ObservableList<String> employeeTypeList = FXCollections.observableArrayList("Admin","Analysts","Executive");
	
    @FXML
    private Button reg;

    @FXML
    private PasswordField pass;

    @FXML
    private TextField username;

    @FXML
    private PasswordField pass1;

    @FXML
    private Label invalid;

    @FXML
    private TextField email;

    @FXML
    private Button back;
    
    //## added by ss
    @FXML
    private ComboBox<String> employeeType;
    
  
    private UserUtils userDao;
    

    public void initialize(URL location, ResourceBundle resources) {
    	
    	userDao = BeanUtils.getBean(UserUtils.class);
    	
        CosmeticStyles.addHoverEffect(reg,back);
        pass.setId("txtinput");
        pass1.setId("txtinput");
        email.setId("txtinput");
        username.setId("txtinput");
        employeeType.setValue("Admin");
        employeeType.setItems(employeeTypeList);
        
        reg.setOnAction(new EventHandler<ActionEvent>() 
        {
        public void handle(ActionEvent event) 
        {
                if (pass.getText().contains(" ") || pass.getText().contains(";") || pass.getText().contains("'") || pass.getText().contains("\"")) 
                {
                    System.out.println("bad pass");
                    pass.setText("");
                    pass1.setText("");
                    invalid.setVisible(true);
                }
                if (pass.getText().equals(pass1.getText()) && !pass.getText().equals("")) 
                {
                    System.out.println("okay, pass is good");
                    if (email.getText().toUpperCase().matches(GeneralMethods.emailPattern)) 
                    {
                        System.out.println("mail matches pattern!");
                        try 
                        {
                           
                            String name = username.getText();
                            //## password encrypted.
                            String password = CryptDataHandler.getInstance().encrypt(pass.getText());
                            String mail = email.getText();
                            //## added by ss(TWP)
                            String userType = employeeType.getValue();
                                
                            //User user = new User(name, password, mail, 0);
                            User user = new User(name, password, mail, 0,true,userType);
                            userDao.saveUser(user);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("User created!");
                            alert.setHeaderText(null);
                            alert.setContentText("User " + name + " successfully created.");
                            alert.showAndWait();
                            new Main().replaceSceneContent("/fxml/login.fxml");
                        } 
                        catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        back.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                new Main().replaceSceneContent("/fxml/login.fxml");
            }
        });
    }
}
