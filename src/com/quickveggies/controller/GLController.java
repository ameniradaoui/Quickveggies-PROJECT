package com.quickveggies.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.quickveggies.BeanUtils;
import com.quickveggies.Main;
import com.quickveggies.controller.dashboard.DBuyerController;
import com.quickveggies.dao.GLCodeDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.PartyType;
import com.quickveggies.entities.Supplier;
import com.quickveggies.entities.User;
import com.quickveggies.impl.iGLCodeDao;
import com.quickveggies.model.EntityType;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class GLController  implements Initializable{
	
	
	@FXML
	private TableView<GLCode> table;

	@FXML
	private TableColumn<GLCode, String> gLCode;
	@FXML
	private TableColumn<GLCode, String> nameOfLedger;
	@FXML
	private TableColumn<GLCode, String> accountCode;

	@FXML
	private TableColumn<GLCode, String> accountType;
	
	@FXML
	private TableColumn<GLCode, String> description;
	
	@FXML
	private Pane settingsPane;

	@FXML
	private ScrollPane paneProducts;

	@FXML
	private AnchorPane ancPaneProducts;

	private SessionDataController session = SessionDataController.getInstance();

	@FXML
	private Pane fruitSettingsPane;
	
	@FXML
	private Button btnAddUser;
	
	CheckBox chkBox = new CheckBox();
	public CheckBox getChkBox() {
		return chkBox;
	}

	public void setChkBox(CheckBox chkBox) {
		this.chkBox = chkBox;
	}

	private Object linkedObject = null;

	public Object getLinkedObject() {
		return linkedObject;
	}

	public void setLinkedObject(Object linkedObject) {
		this.linkedObject = linkedObject;
	}

	@Autowired
	private DataSource dataSource;

	private iGLCodeDao gLCodeDao;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private ObservableList<GLCode> gLCodelines = FXCollections.observableArrayList();

	public String selectedValue;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		gLCodeDao = BeanUtils.getBean(GLCodeDao.class);

		gLCodelines.clear();
		List<GLCode> gLCodes = gLCodeDao.getGLCode();
		gLCodelines.addAll(gLCodes);
		

		
		  
		 table.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("gLCode"));
		 table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nameOfLedger"));
		 table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("accountCode"));
		 table.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("accountType"));
		 table.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("description"));

		table.getColumns().setAll(gLCode, nameOfLedger, accountCode,accountType,description, buildActionTableColumn());

		table.setItems(gLCodelines);

		// System.out.println(fruitSettingsPane.getChildren().indexOf(paneProducts));

		
		
//		btnAddUser.setOnAction(new EventHandler<ActionEvent>() {
//
//			@Override
//			public void handle(ActionEvent event) {
//
//				try {
//					gLCodeDao.saveGLCode();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//
//		});

	}
	

//	private TableColumn buildSelectColumn() {
//		TableColumn selectionCol = new TableColumn("Select");
//		selectionCol.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
//			@Override
//			public TableCell<Object, String> call(TableColumn<Object, String> param) {
//				return new SelectionCheckBoxCell(linkedObject);
//			}
//		});
//		selectionCol.setEditable(true);
//		return selectionCol;
//	}
//
//
//	private class SelectionCheckBoxCell<S, T> extends TableCell<S, T> {
//
//		private CheckBox chkBox = null;
//		
//		private Button btn = null ;
//
//		public SelectionCheckBoxCell(Object linkedObject) {
//			super();
//			chkBox = new CheckBox();
//			chkBox.setOnAction(new EventHandler<ActionEvent>() {
//				
//				@Override
//				public void handle(ActionEvent event) {
//					commitEdit(null);
//				}
//			});
//		}
//		
//		
//		@Override
//		public void commitEdit(T value) {
//			super.commitEdit(value);
//			String selectedTitle = null;
//
//			Object row = getTableRow().getItem();
//			// System.out.println("Class of select item is :" + row.getClass());
//			
//				selectedTitle = row.toString();
//				
//			if (linkedObject == null) {
//				final Stage stage = new Stage();
//				stage.centerOnScreen();
//				stage.setTitle("Profile View");
//				stage.initModality(Modality.APPLICATION_MODAL);
//				stage.setOnCloseRequest((WindowEvent event) -> {
//					Main.getStage().getScene().getRoot().setEffect(null);
//				});
//				getTableView().getScene().getWindow().hide();
//			} else {
//			
//				GLController.this.selectedValue = selectedTitle;
//				getTableView().getScene().getWindow().hide();
//			} // end of behavior-determining section
//		}
//}
//	
//	
//

	private TableColumn buildActionTableColumn() {
		TableColumn action = new TableColumn("Select");
		action.setCellValueFactory(new PropertyValueFactory("title"));
		action.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			@Override
			public TableCell<Object, String> call(TableColumn<Object, String> param) {
				final TableCell<Object, String> cell = new TableCell<Object, String>() {
					
					CheckBox chkBox = new CheckBox();
					@Override
					public void updateItem(final String item, boolean empty) {
						Object rowItem = this.getTableRow();
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							// afficher btn view
							setGraphic(chkBox);

							if (rowItem != null) {
		
								
//								chkBox.setOnAction((ActionEvent event) -> {
									
								//GLCode glCode =  table.getSelectionModel().getSelectedItem();
								//String name = glCode.getNameOfLedger();
								//System.out.println(name);
//									
//									
//									MoneyPaidRecdController.txtgl = new TextField();
//									MoneyPaidRecdController.txtgl.setText("aaa");
									
						
								  /////////////////////
								            
									
								chkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
//
//									
//
									@Override
									public void changed(ObservableValue<? extends Boolean> observable,
											Boolean oldValue, Boolean newValue) {
//											// TODO Auto-generated method stub
										
										
									       
											if (chkBox.isSelected()){
//											GLCode glCode =  table.getSelectionModel().getSelectedItem();
//											String name = glCode.getNameOfLedger();
//											System.out.println(name);
											String selectedTitle = null;
											GLCode glCode = (GLCode) getTableRow().getItem();
											String name = glCode.getNameOfLedger();
											//System.out.println(name);
											TextField name2 = new TextField();
											name2.setText(name);
											//System.out.println( name2.textProperty().getValue());
											
											
											
											
											
											
											//FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/moneypaid.fxml"));
											 MoneyPaidRecdController cont = new MoneyPaidRecdController();//calling class controller
											//loader.setController(cont);
											//Parent root1 = loader.load();
											// Stage stage = new Stage();
											//stage.setScene(new Scene(root1));
											//MoneyPaidRecdController controller = loader.getController();
											//MoneyPaidRecdController.txtgl = new TextField();
											GLCode glCode2 =  (GLCode) getTableRow().getItem();
											String nameL = glCode2.getNameOfLedger();
											System.out.println(glCode2);
											cont.initData(nameL);
											//MoneyPaidRecdController.txtgl.textProperty().bind(name2.textProperty());
											//MoneyPaidRecdController.txtgl.setText(name);
											//stage.show();
											  Stage stage = (Stage) getScene().getWindow();
											    // do what you have to do
											    stage.close();
											
//											try {
//												 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/moneypaid.fxml"));
//								                 Parent root1;
//												 root1 = (Parent) fxmlLoader.load();
//												 Stage stage = new Stage();
//								                 stage.setScene(new Scene(root1));  
//								                 stage.show();
//										        
//								                 MoneyPaidRecdController.txtgl = new TextField();
//													MoneyPaidRecdController.txtgl.setText(name);
//										        MoneyPaidRecdController c = (MoneyPaidRecdController) fxmlLoader.getController();
//										        c.setTxtgl(MoneyPaidRecdController.txtgl);
//										       
//											} catch (IOException e) {
//												// TODO Auto-generated catch block
//												e.printStackTrace();
//											}
//											MoneyPaidRecdController.txtgl = new TextField();
											//binding from table to textfield
//											MoneyPaidRecdController.txtgl.textProperty().bind(name2.textProperty());
//											MoneyPaidRecdController.txtgl.setText(name);
//											
											
									
											
											
											    }
											
											
											
											
									}
//
										
//									});
								
								    //table.getItems().removeAll(table.getSelectionModel().getSelectedItem());
								   //delete action
								     
					          
					                    
					                   
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
