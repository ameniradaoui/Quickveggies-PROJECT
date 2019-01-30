package com.quickveggies.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.mail.Session;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.PropertyAccessorFactory;

import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.dao.BuyerDao;
import com.quickveggies.dao.JournalDao;
import com.quickveggies.dao.MoneyPaidRecdDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.Journal;
import com.quickveggies.entities.MoneyPaidRecd;
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

public class JournalEntryController implements Initializable {

	@FXML
	private Pane imagePanel;
	@FXML
	private TableView table;

	@FXML
	private TextArea memotxt = new TextArea() ;
	@FXML
	private TableColumn<Journal, String> account;
	@FXML
	private TableColumn<Journal, String> debits;
	@FXML
	private TableColumn<Journal, String> credits;
	@FXML
	private TableColumn<Journal, String> description;
	@FXML
	private TableColumn<Journal, String> name;

	@FXML
	private DatePicker dtpDate;
	@FXML
	private Button addButton;		 
	@FXML
	private Button btnUploadImage;
	@FXML
	private Button clear;
	@FXML
	private Button SaveButton;
	private File imgFile;
	@FXML
	private ImageView imvExpense;

	private JournalDao jDao;
	private Long generatedKey = null;

	private ObservableList<Journal> data = FXCollections.observableArrayList();
	// static final String journals[] = {
	// "account",
	// "debits",
	// "description",
	// "name",
	// "credits"};

	final TextField addaccount = new TextField();
	final TextField adddebits = new TextField();
	final TextField adddescription = new TextField();
	final TextField addcredits = new TextField();
	final TextField addname = new TextField();
	private List<Journal> journalList = new ArrayList<>();
	private ObservableList<Journal> journallines = FXCollections.observableArrayList();


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		 jDao = BeanUtils.getBean(JournalDao.class);
		 
		 LocalDate date = LocalDate.now();
		 dtpDate.setValue(date);
		 
		

			data.clear();
			List<Journal> journals = jDao.getJournals();
			data.addAll(journals);
		
		 //memotxt.setText("here");
		 
		 
		 memotxt.setOnKeyReleased(new EventHandler<KeyEvent>() {
		        public void handle(KeyEvent ke) {
		            System.out.println("Key Released: " + ke.getText());
		            
		           memotxt.setText(ke.getText());
		           
		        }});
		 
		 

//		addButton.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent e) {
//				data.add(new Journal("new", "new", "new", "new", "new"));
//			}
//		});
		final HBox hb = new HBox();

//		Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
//			@Override
//			public TableCell call(TableColumn p) {
//				return new EditingCell();
//			}
//		};
		 Callback<TableColumn<Journal, String>, 
         TableCell<Journal, String>> cellFactory
             = (TableColumn<Journal, String> p) -> new EditingCell();
		table.setEditable(true);

		account.setCellValueFactory(new PropertyValueFactory<Journal, String>("account"));
		table.getColumns().add(account);
		account.setCellFactory(cellFactory);
		account.setOnEditCommit(
	            (CellEditEvent<Journal, String> t) -> {
	                ((Journal) t.getTableView().getItems().get(
	                        t.getTablePosition().getRow())
	                        ).setAccount(t.getNewValue());
	                addaccount.setText(t.getNewValue());
	        });

		// TableColumn firstEmailCol = new TableColumn("Primary");
		// TableColumn secondEmailCol = new TableColumn("Secondary");
		//
		// emailCol.getColumns().addAll(firstEmailCol, secondEmailCol);

		debits.setCellValueFactory(new PropertyValueFactory<Journal, String>("debits"));
		table.getColumns().add(debits);
		debits.setCellFactory(cellFactory);
		debits.setOnEditCommit(
	            (CellEditEvent<Journal, String> t) -> {
	                ((Journal) t.getTableView().getItems().get(
	                        t.getTablePosition().getRow())
	                        ).setDebits(t.getNewValue());
	                adddebits.setText(t.getNewValue());
	        });

		description.setCellValueFactory(new PropertyValueFactory<Journal, String>("description"));
		table.getColumns().add(description);
		description.setCellFactory(cellFactory);
		description.setOnEditCommit(
	            (CellEditEvent<Journal, String> t) -> {
	                ((Journal) t.getTableView().getItems().get(
	                        t.getTablePosition().getRow())
	                        ).setDescription(t.getNewValue());
	                System.out.println(t.getNewValue());
	                adddescription.setText(t.getNewValue());
	        });

		name.setCellValueFactory(new PropertyValueFactory<Journal, String>("name"));
		table.getColumns().add(name);
		name.setCellFactory(cellFactory);
		name.setOnEditCommit(
	            (CellEditEvent<Journal, String> t) -> {
	                ((Journal) t.getTableView().getItems().get(
	                        t.getTablePosition().getRow())
	                        ).setName(t.getNewValue());
	                addname.setText(t.getNewValue());
	        });

		credits.setCellValueFactory(new PropertyValueFactory<Journal, String>("credits"));
		table.getColumns().add(credits);
		credits.setCellFactory(cellFactory);
		credits.setOnEditCommit(
	            (CellEditEvent<Journal, String> t) -> {
	                ((Journal) t.getTableView().getItems().get(
	                        t.getTablePosition().getRow())
	                        ).setCredits(t.getNewValue());
	                addcredits.setText(t.getNewValue());
	        });
		
		//
//		Journal journal = new Journal();
//		journal.setAccount(account.getText());
//		journal.setJournalDate(dtpDate.getValue().toString());
//		//journal.setAttachement(attachement);
//		journalList.add(journal);
		table.getColumns().setAll(account, debits, description, name, credits ,buildActionTableColumn());
		table.setItems(data);
		//table.setItems(journallines);
		
		

		
		//addaccount.setText(account.getText());
		addaccount.setMaxWidth(account.getPrefWidth());
		
		//adddebits.setText("new");
		adddebits.setMaxWidth(debits.getPrefWidth());
		
		adddescription.setMaxWidth(description.getPrefWidth());
		//adddescription.setText("new ");
		
		
		addcredits.setMaxWidth(credits.getPrefWidth());
		//addcredits.setText("new");
		
		addname.setMaxWidth(name.getPrefWidth());
		//addname.setText("new");
		

		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				data.add(new Journal(addaccount.getText(),
						adddebits.getText(), 
						addcredits.getText(),
						adddescription.getText(), 
						addname.getText()
						  
						 ));
				addaccount.clear();
				adddebits.clear();
				addcredits.clear();
				adddescription.clear();
				addname.clear();
				
				
				
				
				
				
			}
		});
		System.out.println(addaccount.getText());
		hb.getChildren().addAll(addaccount, adddebits, addcredits, adddescription,addname);
		
		 btnUploadImage.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					//System.out.println("I'm here");
					uploadImage(event);
					
				}
			});
		 clear.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(ActionEvent event) {
					System.out.println("I'm here");
					removeAllRows();
					
				}
			});
		

		// Editable columns
		// for(int i=0; i<journals.length; i++){
		// TableColumn col = new TableColumn(journals[i]);
		// col.setCellValueFactory(
		// new PropertyValueFactory<Journal, String>(
		// "value_" + String.valueOf(i)));
		// table.getColumns().add(col);
		// col.setCellFactory(cellFactory);
		// }
		// table.setItems(data);
		// Stage primaryStage = null ;
		// Group root = new Group();
		// VBox vBox = new VBox();
		// vBox.setSpacing(10);
		// vBox.getChildren().addAll(addButton, table);
		// root.getChildren().add(vBox);
		
		
		SaveButton.setOnAction((ActionEvent event) -> {
	           
	         
					try {
						saveMprObject();
					} catch (Exception e1) {
						System.out.println("error on save Journal");
					}
				
	            SaveButton.getScene().getWindow().hide();
	        });

	}

	private Object buildActionTableColumn() {

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
									  Journal journal  =  (Journal) getTableRow().getItem();
								      //Journal journal =(Journal)table.getSelectionModel().getSelectedItem();
								      System.out.println(journal);
								      
								       Long id = journal.getJournalNo();
					                	jDao.deleteJournal(id);
					                	data.remove(journal);
					                	
					          
					                    
					                   
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

	protected void removeAllRows() {
		for ( int i = 0; i<table.getItems().size(); i++) {
	        table.getItems().clear(); 
	    } 
		
	}

	private void saveMprObject() throws IOException    {
		generatedKey = null;
//		System.out.println("size" +journalList.size());
//		for (Journal journal:journalList) {
			System.out.println(dtpDate.getValue().toString());
			Journal mpr = new Journal();
			mpr.setAccount(addaccount.getText());
			mpr.setName(addname.getText());
			mpr.setCredits(addcredits.getText());
			mpr.setDebits(adddebits.getText());
			mpr.setDescription(adddescription.getText());
			mpr.setJournalDate(dtpDate.getValue().toString());
			mpr.setMemo(memotxt.getText());
			if (imgFile != null && imgFile.exists()) {
					
							try {
								mpr.setAttachement(new FileInputStream(imgFile));
							} catch (Exception e) {
								e.printStackTrace();
								throw e;
							}
				
			}

			generatedKey = jDao.insertJournal(mpr);
			
		}
		

	

	private String getNormalizedValue(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	public void uploadImage(final Event event) {
		System.out.println("image");
		;
		if (!(event.getSource() instanceof Node))
			throw new IllegalArgumentException(
					"The source of the event should be instance of java FX node or any of its' subclass");
		Window mainStage = ((Node) event.getSource()).getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters()
				.addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
		File selectedFile = fileChooser.showOpenDialog(mainStage);
		if (selectedFile != null) {
			try (InputStream is = new FileInputStream(selectedFile)) {
				imgFile = selectedFile;
				imvExpense.setImage(new Image(is));
				imagePanel.setStyle("-fx-border-color: none;");
			} catch (FileNotFoundException e) {
				GeneralMethods.errorMsg("Cannot find specified file:" + selectedFile.getName());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
