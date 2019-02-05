package com.quickveggies.controller;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.quickveggies.BeanUtils;
import com.quickveggies.UserGlobalParameters;
import com.quickveggies.dao.BuyerDao;
import com.quickveggies.dao.CompanyDao;
import com.quickveggies.dao.OptionsDao;
import com.quickveggies.dao.SupplierDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.Company;
import com.quickveggies.entities.CompanyNameSelection;
import com.quickveggies.entities.EmailUtils;
import com.quickveggies.entities.JavaEmail;
import com.quickveggies.entities.JavaSMS;
import com.quickveggies.entities.JavaWHATSAPP;
import com.quickveggies.entities.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.EventHandler;

public class SMSTemplateController implements Initializable {

	@FXML
	private Button btnEditTemplate;

	@FXML
	private Button btnsms;

	@FXML
	private TableView<CompanyNameSelection> table;

	@FXML
	private Button btnemail;

	@FXML
	private TableColumn<CompanyNameSelection, String> company;

	@FXML
	private Button btnwhatsapp;

	@FXML
	private Button sms;
	@FXML
	private Button email;
	@FXML
	private Button whatsup;

	@FXML
	private Button btnSaveTemplate;

	@FXML
	private Button btnBuyer;
	@FXML
	private Button btnSupplier;
	@FXML
	private Button btnReminder;

	private OptionsDao optionsDao;

	@FXML
	private TextArea taSMSTemplate;
	private static final String SMSTemplate = "Dear %s your purchases for date {Date} is valued {TotalAmt} ."
			+ "Pay within 3 days and avail a flat 1% cashback." + "               Regards"
			+ "                                                        SuperSalesArgo.";
	// String content ="";

	private static final String EmailTemplate = "Dear %s " + "" + ""
			+ "                                                     We would like to remind you that your purchases for date {Date} is valued {TotalAmt} ."
			+ "Pay within 3 days and avail a flat 1 percent cashback."
			+ "                                   With best regards                                 " + " "
			+ "                      The Team.";

	private static final String WHATSAPPPTemplate = "Hi ${PartyName} your purchases for date ${Date} is valued ${TotalAmt} ."
			+ "Pay within 3 days and avail a flat 1% cashback.";

	DropShadow shadow = new DropShadow();

	private CompanyDao companyDao;
	private BuyerDao bd;
	private SupplierDao sp;
	private String choice;
	JavaEmail javaEmail = new JavaEmail();
	JavaSMS javaSMS = new JavaSMS();
	JavaWHATSAPP javawhatsapp = new JavaWHATSAPP();
	private ObservableList<CompanyNameSelection> companylines = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		companyDao = BeanUtils.getBean(CompanyDao.class);
		optionsDao = BeanUtils.getBean(OptionsDao.class);
		bd = BeanUtils.getBean(BuyerDao.class);
		sp = BeanUtils.getBean(SupplierDao.class);

		companylines.clear();
		List<CompanyNameSelection> comp = companyDao.getAllCompany();
		companylines.addAll(comp);

		table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("name"));

		table.getColumns().setAll(company);

		table.setItems(companylines);

		taSMSTemplate.setEditable(false);
		taSMSTemplate.setText(UserGlobalParameters.GET_SMS_TEMPLATE());
		taSMSTemplate.setWrapText(true);
		btnEditTemplate.setOnAction((event) -> {
			taSMSTemplate.setEditable(true);
			btnEditTemplate.setDisable(true);
			btnSaveTemplate.setDisable(false);
		});
		btnSaveTemplate.setOnAction((event) -> {
			btnEditTemplate.setDisable(false);
			UserGlobalParameters.SET_SMS_TEMPLATE(taSMSTemplate.getText(), true);
			btnSaveTemplate.setDisable(true);
			taSMSTemplate.setEditable(false);
		});

		sms.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				sms.setStyle("-fx-background-color: Red");
				taSMSTemplate.setText(SMSTemplate);
				choice = "SMS";
				System.out.println(optionsDao.getConfigSms());

			}
		});
		sms.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				sms.setStyle("-fx-background-color: AliceBlue");
			}
		});

		email.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				email.setStyle("-fx-background-color: Red");

				taSMSTemplate.setText(EmailTemplate);
				choice = "EMAIL";
				// System.out.println(optionsDao.getConfigEmail());
			}
		});
		email.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				email.setStyle("-fx-background-color: AliceBlue");
			}
		});

		whatsup.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				whatsup.setStyle("-fx-background-color: Red");
				taSMSTemplate.setText(WHATSAPPPTemplate);
				choice = "WHATSAPP";
				System.out.println(optionsDao.getConfigWhatsapp());
			}
		});
		whatsup.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				whatsup.setStyle("-fx-background-color: AliceBlue");
			}
		});

		btnBuyer.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				btnBuyer.setEffect(shadow);
				List<String> addr = bd.getEmails();
				List<String> mobile = bd.getSMS();

				Map<String, String> mapemail = new HashMap<>();

				mapemail = bd.getEmailMapper();

				if (choice.equals("EMAIL")) {

					//for (String key : mapemail.keySet()) {
					//	System.out.println("Key:" + key + " Value:" + mapemail.get(key));
						javaEmail.setMailServerProperties(mapemail, taSMSTemplate.getText());

					//}
				}

				// Set<String> addresses = new HashSet<>();
				// addresses.add(addr);
				System.out.println(addr);

				if (choice.equals("SMS")) {
					javaSMS.setSmsServerProperties(mobile, taSMSTemplate.getText());

				}
				if (choice.equals("WHATSAPP")) {
					try {
						javawhatsapp.sendMessage(mobile, taSMSTemplate.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}
		});

		btnBuyer.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				btnBuyer.setEffect(null);
			}
		});
		btnSupplier.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				btnSupplier.setEffect(shadow);
				// System.out.println(taSMSTemplate.getText() + " " + choice);
				List<String> addr = sp.getEmails();
				List<String> mobile = sp.getSMS();
				System.out.println(addr);

				
				
				Map<String, String> mapemail = new HashMap<>();

				mapemail = sp.getEmailMapper();

				if (choice.equals("EMAIL")) {

					//for (String key : mapemail.keySet()) {
					//	System.out.println("Key:" + key + " Value:" + mapemail.get(key));
						javaEmail.setMailServerProperties(mapemail, taSMSTemplate.getText());

					//}
				}

				else if (choice.equals("SMS")) {
					javaSMS.setSmsServerProperties(mobile, taSMSTemplate.getText());

				}

				else if (choice.equals("WHATSAPP")) {
					try {
						javawhatsapp.sendMessage(mobile, taSMSTemplate.getText());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});

		btnSupplier.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				btnSupplier.setEffect(null);
			}
		});
		btnReminder.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				btnReminder.setEffect(shadow);
			}
		});

		btnReminder.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				btnReminder.setEffect(null);
			}
		});

		btnsms.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// new Main().replaceSceneContent("/fxml/glCodes.fxml");

				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/configureSmsAPI.fxml"));
					Parent root1;
					root1 = (Parent) fxmlLoader.load();
					Stage stage = new Stage();
					stage.setScene(new Scene(root1));
					stage.show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		btnemail.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// new Main().replaceSceneContent("/fxml/glCodes.fxml");

				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/configureEmailAPI.fxml"));
					Parent root1;
					root1 = (Parent) fxmlLoader.load();
					Stage stage = new Stage();
					stage.setScene(new Scene(root1));
					stage.show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
		btnwhatsapp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// new Main().replaceSceneContent("/fxml/glCodes.fxml");

				try {
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/configureWhatsUpAPI.fxml"));
					Parent root1;
					root1 = (Parent) fxmlLoader.load();
					Stage stage = new Stage();
					stage.setScene(new Scene(root1));
					stage.show();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

	}

}
