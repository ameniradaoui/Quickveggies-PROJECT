package com.quickveggies.controller;

import java.awt.Window.Type;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.omg.CORBA.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;

import com.quickveggies.misc.FruitButtonEventHandler;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import com.quickveggies.BeanUtils;
import com.quickveggies.Main;
import com.quickveggies.dao.StorageBuyerDealDao;
import com.quickveggies.dao.SupplierDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TableView;

public class SettingsController implements Initializable {

	@FXML
	private Button btnAddUser;

	@FXML
	private TableView<User> table;

	@FXML
	private TableColumn<User, String> name;
	@FXML
	private TableColumn<User, String> usertype;
	@FXML
	private TableColumn<User, String> email;

	@FXML
	private Pane settingsPane;

	@FXML
	private ScrollPane paneProducts;

	@FXML
	private AnchorPane ancPaneProducts;

	private SessionDataController session = SessionDataController.getInstance();

	@FXML
	private Pane fruitSettingsPane;

	@Autowired
	private DataSource dataSource;

	private UserUtils userUtils;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private ObservableList<User> userlines = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		userUtils = BeanUtils.getBean(UserUtils.class);

		userlines.clear();
		List<User> users = userUtils.geUser();
		userlines.addAll(users);

		
		  
		 table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		 table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("email"));
		 table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("usertype"));

		table.getColumns().setAll(name, usertype, email, buildActionTableColumn());

		table.setItems(userlines);

		// System.out.println(fruitSettingsPane.getChildren().indexOf(paneProducts));

		btnAddUser.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				new Main().replaceSceneContent("/fxml/register.fxml");
			}

		});

	}

	private TableColumn buildActionTableColumn() {
		TableColumn action = new TableColumn("Action");
		action.setCellValueFactory(new PropertyValueFactory("title"));
		action.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			@Override
			public TableCell<Object, String> call(TableColumn<Object, String> param) {
				final TableCell<Object, String> cell = new TableCell<Object, String>() {
					Button btn = new Button("Delete");

					@Override
					public void updateItem(final String item, boolean empty) {
						Object rowItem = this.getTableRow();
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							// afficher btn view
							setGraphic(btn);

							if (rowItem != null) {
								btn.setOnAction((ActionEvent event) -> {
									
								    //table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
								   //delete action
								      User user =(User)table.getSelectionModel().getSelectedItem();
								      System.out.println(user);
								      
								       Integer id = user.getId();
					                	userUtils.deleteUser(id);
					                	userlines.remove(user);
					                	
					          
					                    
					                   
								});
							}

						}
					}
				};
				return cell;
			}
		});
		action.setEditable(false);
		action.setStyle("-fx-pref-width: 80;");
		return action;
	}

}
