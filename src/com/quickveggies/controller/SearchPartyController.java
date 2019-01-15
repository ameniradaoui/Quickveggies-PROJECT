package com.quickveggies.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import com.itextpdf.text.pdf.TextField;
import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.Main;
import com.quickveggies.dao.BuyerDao;
import com.quickveggies.dao.DatabaseClient;
import com.quickveggies.dao.ExpenditureDao;
import com.quickveggies.dao.SupplierDao;
import com.quickveggies.entities.ArrivalSelectionFilter;
import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.BuyerSelectionFilter;
import com.quickveggies.entities.PartyType;
import com.quickveggies.entities.Supplier;
import com.quickveggies.misc.AutoCompleteTextField;
import com.quickveggies.model.EntityType;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class SearchPartyController implements Initializable {

	@FXML
	private Button cancelButton;
	@FXML
	private TableView resultsTable;
	@FXML
	private AutoCompleteTextField searchbar;
	@FXML
	private ComboBox<PartyType> profileType;
	@FXML
	private Button startSearchButton;

	private String selectedValue;

	EntityType personType = null;

	private DatabaseClient dbclient;

	private BuyerDao bd;

	private SupplierDao supplierDao;

	private ExpenditureDao ed;

	private PartyType partyType;
	private Object linkedObject = null;

	public SearchPartyController(PartyType partyType, Object linkedObject) {
		this.partyType = partyType;
		this.linkedObject = linkedObject;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ed = BeanUtils.getBean(ExpenditureDao.class);
		supplierDao = BeanUtils.getBean(SupplierDao.class);
		bd = BeanUtils.getBean(BuyerDao.class);
		dbclient = BeanUtils.getBean(DatabaseClient.class);

		profileType.getItems().addAll(PartyType.values());
		profileType.setValue(PartyType.BUYER_SUPPLIERS);
		partyType = profileType.getValue();
		setupSearchAction();
		/*
		 * searchbar.setEntries(
		 * profilesTitlesFromEntities(searchPartyInDatabase(
		 * SearchPartyController.BUYER_SUPPLIERS, null)));
		 */
		// populate the searchbar options
		// searchbar.setOnMousePressed((Event event) -> {
		// if (PartyType.EXPENDITURE_TYPES.equals(profileType.getValue())) {
		// searchbar.setEntries(profilesTitlesFromEntities(
		// searchPartyInDatabase(profileType.getValue(), null)));
		// }
		// else {
		// searchbar.setEntries(new TreeSet<>(searchPartyInDatabase(
		// profileType.getValue(), null)));
		// }
		// });
		resetTableStructure();
		resultsTable.setItems(FXCollections.observableArrayList(searchPartyInDatabase(partyType, null)));

		profileType.valueProperty().addListener(
				(ObservableValue<? extends PartyType> observable, PartyType oldValue, PartyType newValue) -> {
					partyType = newValue;
					resetTableStructure();
					ObservableList<?> list = FXCollections.observableArrayList(searchPartyInDatabase(partyType, null));
					GeneralMethods.refreshTableView(resultsTable, list);
				});
		startSearchButton.setOnAction((ActionEvent event) -> {
			partyType = profileType.getValue();
			GeneralMethods.refreshTableView(resultsTable, FXCollections
					.observableArrayList(searchPartyInDatabase(profileType.getValue(), searchbar.getText())));
		});
		cancelButton.setOnAction((ActionEvent event) -> {
			cancelButton.getScene().getWindow().hide();
		});
	}

	private void setupSearchAction() {
//////////////////////////////////////////// trying to find a solution for the autocomplete textfield
//		List<BuyerSelectionFilter> selectionFiler = bd.getSelectionFiler();
//  	  List<String> list5 = new ArrayList<String>();
//  	for (BuyerSelectionFilter buyerSelectionFilter : selectionFiler) {
//			list5.add(buyerSelectionFilter.getTitle());
//		}
//       ObservableList obList5 = FXCollections.observableList(list5);
//     
//       System.out.println(obList5);
//     //////////////////////////////////////////////
//       
//       
//		List<String> listB = new ArrayList<String>();
//		List<String> listS = new ArrayList<String>();
//
//		Buyer b = new Buyer();
//		Supplier s = new Supplier();
//		listB.add(b.getTitle());
//		listS.add(s.getTitle());
//		ObservableList obList2 = FXCollections.observableList(listS);
//		ObservableList obList = FXCollections.observableList(listB);
//		


		searchbar.textProperty().addListener((observable, oldValue, newValue) -> {
			BuyerSelectionFilter filter = BuyerSelectionFilter.fromString(newValue);
			
			List<Buyer> list = bd.getListByBuyerTitle(newValue);
			List<Supplier> list2 = supplierDao.getListBySupplierTitle(newValue);
			switch (partyType) {
			case BUYERS:
				resultsTable.getItems().clear();
				resultsTable.getItems().addAll(list);
				break;
			case SUPPLIERS:
				resultsTable.getItems().clear();
				resultsTable.getItems().addAll(list2);
				break;
			case BUYER_SUPPLIERS:
				resultsTable.getItems().clear();
				resultsTable.getItems().addAll(list);
				resultsTable.getItems().addAll(list2);
				break;
			}

		});

	}

	private void resetTableStructure() {
		if (!PartyType.EXPENDITURE_TYPES.equals(partyType) && !PartyType.UNIVERSAL.equals(partyType)) {
			buildBuyerSupplierTable();
		} else {
			buildExpenditureTable();
		}
	}

	private void buildExpenditureTable() {
		TableColumn<String, String> typeCol = new TableColumn("Name");
		typeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<String, String> param) {
						return new SimpleStringProperty(param.getValue());
					}
				});
		// typeCol.setCellFactory();
		typeCol.setEditable(false);
		typeCol.setPrefWidth(150);

		resultsTable.getColumns().setAll(buildSelectColumn(), typeCol, buildViewTableColumn());
	}

	private void buildBuyerSupplierTable() {
		resultsTable.setEditable(true);

		TableColumn titleCol = new TableColumn("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory("title"));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
		titleCol.setEditable(false);
		TableColumn typeCol = new TableColumn("Type");
		typeCol.setCellValueFactory(new PropertyValueFactory("type"));
		typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		typeCol.setEditable(false);

		TableColumn buyerTypeCol = new TableColumn("Sub. Categ.");
		buyerTypeCol.setCellValueFactory(new PropertyValueFactory("buyerType"));
		buyerTypeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		buyerTypeCol.setEditable(false);

		TableColumn firstNameCol = new TableColumn("First Name");
		firstNameCol.setCellValueFactory(new PropertyValueFactory("firstName"));
		firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		firstNameCol.setEditable(false);

		TableColumn lastNameCol = new TableColumn("Last Name");
		lastNameCol.setCellValueFactory(new PropertyValueFactory("lastName"));
		lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		lastNameCol.setEditable(false);

		TableColumn companyCol = new TableColumn("Company");
		companyCol.setCellValueFactory(new PropertyValueFactory("company"));
		companyCol.setCellFactory(TextFieldTableCell.forTableColumn());
		companyCol.setEditable(false);

		resultsTable.getColumns().setAll(buildSelectColumn(), titleCol, typeCol, buyerTypeCol, firstNameCol,
				lastNameCol, companyCol, buildViewTableColumn());

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableColumn buildSelectColumn() {
		TableColumn selectionCol = new TableColumn("Select");
		selectionCol.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			@Override
			public TableCell<Object, String> call(TableColumn<Object, String> param) {
				return new SelectionCheckBoxCell(linkedObject);
			}
		});
		selectionCol.setEditable(true);
		return selectionCol;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableColumn buildViewTableColumn() {
		TableColumn viewCol = new TableColumn("View");
		viewCol.setCellValueFactory(new PropertyValueFactory("title"));
		viewCol.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			@Override
			public TableCell<Object, String> call(TableColumn<Object, String> param) {
				final TableCell<Object, String> cell = new TableCell<Object, String>() {
					Button btn = new Button("view");
					

					@Override
					public void updateItem(final String item, boolean empty) {
						 Object rowItem = this.getTableRow();
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							//afficher btn view
							setGraphic(btn);
							
							 
				      
							if (rowItem != null) {
								btn.setOnAction((ActionEvent event) -> {
									  final Stage view = new Stage();
								        view.centerOnScreen();
								        view.setTitle("view party");
								        view.initModality(Modality.APPLICATION_MODAL);
								        view.setOnCloseRequest(new EventHandler<WindowEvent>() {
								            public void handle(WindowEvent event) {
								                Main.getStage().getScene().getRoot().setEffect(null);
								            }
								        });
								        try {
								            Parent parent = FXMLLoader.load(ProfileViewController.class.getResource("/fxml/profileview.fxml"));
								            Scene scene = new Scene(parent, 680, 600);
								            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
								                public void handle(KeyEvent event) {
								                    if (event.getCode() == KeyCode.ESCAPE) {
								                        Main.getStage().getScene().getRoot().setEffect(null);
								                        view.close();
								                    }
								                }
								            });
								            view.setScene(scene);
								            view.show();
								        } catch (IOException e) {
								            e.printStackTrace();
								        }
									
									
//									EntityType personType1 = null;
//									System.out.println(rowItem.getClass().getName());
//									if (rowItem instanceof Supplier) {
//										personType1 = EntityType.SUPPLIER;
//									} else if (rowItem instanceof Buyer) {
//										personType1 = EntityType.BUYER;
//									}
//									GeneralMethods.openNewWindow(SearchPartyController.this, "/fxml/profileview.fxml",
//											new ProfileViewController(item, personType1), null);
								});
							}
							
						}
					}
				};
				return cell;
			}
		});
		viewCol.setEditable(false);
		viewCol.setStyle("-fx-pref-width: 80;");
		return viewCol;
	}
//test
//	private void showNewBuyerDialog() {
//	       final Stage view = new Stage();
//	        view.centerOnScreen();
//	        view.setTitle("view party");
//	        view.initModality(Modality.APPLICATION_MODAL);
//	        view.setOnCloseRequest(new EventHandler<WindowEvent>() {
//	            public void handle(WindowEvent event) {
//	                Main.getStage().getScene().getRoot().setEffect(null);
//	            }
//	        });
//	        try {
//	            Parent parent = FXMLLoader.load(SearchPartyController.class.getResource("/fxml/profileview.fxml"));
//	            Scene scene = new Scene(parent, 687, 400);
//	            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
//	                public void handle(KeyEvent event) {
//	                    if (event.getCode() == KeyCode.ESCAPE) {
//	                        Main.getStage().getScene().getRoot().setEffect(null);
//	                        view.close();
//	                    }
//	                }
//	            });
//	            view.setScene(scene);
//	            view.show();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//		
//	}



	
	private ArrayList searchPartyInDatabase(PartyType partyType, String keyword) {
		ArrayList result = null;

		switch (partyType) {
		case BUYERS:
			result = getDatabaseBuyersList(keyword, null);
			return result;
		case LADAAN:
			result = getDatabaseBuyersList(keyword, "Ladaan");
			return result;
		case BIJAK:
			result = getDatabaseBuyersList(keyword, "Bijak");
			return result;
		case SUPPLIERS:
			result = getDatabaseSuppliersList(keyword);
			return result;
		case BUYER_SUPPLIERS:
			result = getDatabaseBuyersList(keyword, null);
			result.addAll(getDatabaseSuppliersList(keyword));
			return result;
		case EXPENDITURE_TYPES:
			result = getDatabaseExpenditureTypeList(keyword);
			return result;
		case UNIVERSAL:
			result = new ArrayList<>();
			result.add("Cash A/C");
			result.add("Bank A/C");
			return result;
		default:
			System.out.println("unrecognized partyType");
			return result;
		}
	}

	private ArrayList<Supplier> getDatabaseSuppliersList(String keyword) {

		int rowsNum = dbclient.getRowsNum("suppliers1");
		ArrayList<Supplier> result = new ArrayList<>();
		for (int supp_id = 1; supp_id <= rowsNum; supp_id++) {
			try {
				if (keyword == null) {
					result.add(supplierDao.getSupplierById(supp_id));
				} else {
					Supplier supplier = supplierDao.getSupplierById(supp_id);
					if (supplier != null)
						if ((supplier.getTitle().toLowerCase()).contains((keyword).toLowerCase())) {
							result.add(supplier);
						}
				}
			} catch (java.sql.SQLException e) {
				System.out.print("sqlexception in populating suppliers list in SearchPartyController");
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList getDatabaseExpenditureTypeList(String keyword) {

		ArrayList result = new ArrayList<>();
		List<String> tmpList = ed.getExpenditureTypeList();
		if (keyword != null) {
			for (String xType : tmpList) {
				if (xType.equalsIgnoreCase(keyword)) {
					result.add(xType);
				}
			}
		} else {
			result.addAll(tmpList);
		}
		return result;
	}

	private ArrayList<Buyer> getDatabaseBuyersList(String keyword, String subtype) {

		int rowsNum = dbclient.getRowsNum("buyers1");
		ArrayList<Buyer> result = new ArrayList<>();
		for (int buyer_id = 1; buyer_id <= rowsNum; buyer_id++) {
			try {
				Buyer buyer = bd.getBuyerById(buyer_id);
				String title = buyer.getTitle().toLowerCase();
				if ("cold store".equals(title) || "godown".equals(title) || "cash sale".equals(title)
						|| "bank sale".equals(title)) {
					continue;
				}
				// check if ladaan/bijak
				if (subtype != null && !buyer.getBuyerType().equals(subtype)) {
					continue;
				}
				if (keyword == null) {
					result.add(buyer);
				} else {
					if ((buyer.getTitle().toLowerCase()).contains((keyword).toLowerCase())) {
						result.add(buyer);
					}
				}
			} catch (java.sql.SQLException e) {
				System.out.println("sqlexception in populating buyers list in SearchPartyController");
			}
		}
		return result;
	}

	private TreeSet<String> profilesTitlesFromEntities(ArrayList entities) {
		TreeSet<String> result = new TreeSet<String>();
		for (Object entity : entities) {
			try {
				result.add(((Buyer) entity).getTitle());
			} catch (Exception e) {
				try {
					result.add(((Supplier) entity).getTitle());
				} catch (Exception ex) {
					System.out.println("invalid cast in profilesTitlesFromEntities");
				}
			}
		}

		return result;
	}

	// define what happens upon checkbox selection in this class
	private class SelectionCheckBoxCell<S, T> extends TableCell<S, T> {

		private CheckBox chkBox = null;
		
		private Button btn = null ;

		public SelectionCheckBoxCell(Object linkedObject) {
			super();
			chkBox = new CheckBox();
			chkBox.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					commitEdit(null);
				}
			});
		}

		@Override
		public void commitEdit(T value) {
			super.commitEdit(value);
			String selectedTitle = null;

			Object row = getTableRow().getItem();
			// System.out.println("Class of select item is :" + row.getClass());
			if (PartyType.EXPENDITURE_TYPES.equals(partyType)) {
				selectedTitle = row.toString();
				personType = EntityType.EXPENDITURE;
			} else if (PartyType.UNIVERSAL.equals(partyType)) {
				selectedTitle = row.toString();
				if ("Cash A/C".equalsIgnoreCase((String) row)) {
					personType = EntityType.UNIVERSAL_CASH;
				} else if ("Bank A/C".equalsIgnoreCase((String) row)) {
					personType = EntityType.UNIVERSAL_BANK;
				}
			} else {
				if (row instanceof Supplier) {
					selectedTitle = ((Supplier) (getTableRow().getItem())).getTitle();
					personType = EntityType.SUPPLIER;
				} else if (row instanceof Buyer) {
					selectedTitle = ((Buyer) (getTableRow().getItem())).getTitle();
					personType = EntityType.BUYER;
				}
			}
			// profile search performed from dashboard, open the profile view
			// window
			if (linkedObject == null) {
				final Stage stage = new Stage();
				stage.centerOnScreen();
				stage.setTitle("Profile View");
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.setOnCloseRequest((WindowEvent event) -> {
					Main.getStage().getScene().getRoot().setEffect(null);
				});
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profileview.fxml"));
					ProfileViewController controller = new ProfileViewController(selectedTitle, personType);
					loader.setController(controller);
					Parent parent = loader.load();
					Scene scene = new Scene(parent);
					scene.setOnKeyPressed((KeyEvent event) -> {
						if (event.getCode() == KeyCode.ESCAPE) {
							Main.getStage().getScene().getRoot().setEffect(null);
							stage.close();
						}
					});
					stage.setScene(scene);
					stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}

				getTableView().getScene().getWindow().hide();
			} else {
				/*
				 * // search profile performed from some textfield, determine //
				 * which and act accordingly try {// grower text field
				 * AutoCompleteTextField linkedAutoCompleteField =
				 * ((AutoCompleteTextField) linkedObject);
				 * linkedAutoCompleteField.setText(selectedTitle);
				 * linkedAutoCompleteField.hidePopup();
				 * 
				 * } catch (Exception ex) { try {// buyer table cell -> update
				 * the value in the linked // cell PartySearchTableCell
				 * linkedCell = (PartySearchTableCell) linkedObject;
				 * ((FreshEntryTableData.BuyerEntryTableLine)
				 * linkedCell.getTableRow().getItem())
				 * .setBuyerSelect(selectedTitle);
				 * GeneralMethods.refreshTableView(linkedCell.getTableView(),
				 * null); } catch (Exception e) { try {// account entry line in
				 * accounts dashboard -> // update the value in the linked cell
				 * and save // the payee in the database PartySearchTableCell
				 * linkedCell = (PartySearchTableCell) linkedObject;
				 * AccountEntryLine line = (AccountEntryLine)
				 * linkedCell.getTableRow().getItem();
				 * line.setPayee(selectedTitle);
				 * DatabaseClient.getInstance().updateTableEntry(
				 * "accountEntries", line.getId(), new String[]{"payee"}, new
				 * String[]{line.getPayee()}, false);
				 * GeneralMethods.refreshTableView(linkedCell.getTableView(),
				 * null); } catch (Exception exp) { System.out.println(
				 * "Exception while casting in SearchPartyController\n"); } }
				 * 
				 * }
				 */
				SearchPartyController.this.selectedValue = selectedTitle;
				getTableView().getScene().getWindow().hide();
			} // end of behavior-determining section
		}// end of commit section

		@Override
		public void updateItem(T item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setGraphic(null);
			} else {
				setGraphic(chkBox);
			}
		}
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public EntityType getPartyType() {

		return personType;
	}

	public void setPartyType(PartyType partyType) {
		this.partyType = partyType;
	}

	public Object getLinkedObject() {
		return linkedObject;
	}

	public void setLinkedObject(Object linkedObject) {
		this.linkedObject = linkedObject;
	}
}
