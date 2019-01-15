package com.quickveggies.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.ai.util.dates.DateUtil;
import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.PaymentMethodSource;
import com.quickveggies.dao.DatabaseClient;
import com.quickveggies.dao.ExpenditureDao;

import com.quickveggies.dao.MoneyPaidRecdDao;
import com.quickveggies.dao.SupplierDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.Expenditure;
import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.entities.PartyType;
import com.quickveggies.entities.Supplier;
import com.quickveggies.impl.IExpenditureDao;
import com.quickveggies.impl.IMoneyPaidRecordDao;
import com.quickveggies.misc.AutoCompleteTextField;
import com.quickveggies.misc.SearchPartyButton;
import com.quickveggies.misc.Utils;
import com.quickveggies.model.DaoGeneratedKey;
import com.quickveggies.model.EntityType;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class ExpenseAddController implements Initializable {
	
	

    @FXML
    private TextField amountField;

    @FXML
    private DatePicker dateField;

    @FXML
    private TextField commentField;

    @FXML
    private SearchPartyButton payeeSearchButton;

    @FXML
    private Button btnUploadImage;

    @FXML
    private ImageView imvExpense;

    @FXML
    private ComboBox<String> cboPaymentType;

    @FXML
    private DatePicker dpDepositDate;

    @FXML
    private TextField txtChequeNo;

    @FXML
    private TextField txtBankName;
    @FXML
    private Pane imagePanel;
    
    @FXML
    private Pane paneBankDetails;

    @FXML
    private AutoCompleteTextField searchExpenseType;
    
    @FXML
    private AutoCompleteTextField payeeField;

    @FXML
    private Button create;

    private File imgFile;
    
    private Integer generatedKey = null;
    
    private final PaymentMethodSource defPayMethodSource;
    TreeSet<String> growersList = null;
    
    private static final String STR_ADD_NEW = "Add new...";
    private SupplierDao supplierDao; 
    private  ExpenditureDao ed ;
    
    private  MoneyPaidRecdDao mpd;
    
    private DatabaseClient dbc;
    
    private String defaultAmt, defPartyTitle, expenseType, payeeType, defDate,
            defComment, chequeNo, bankName;
  


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	System.out.println("where are you?");
    	ed = BeanUtils.getBean(ExpenditureDao.class);
    	mpd = BeanUtils.getBean(MoneyPaidRecdDao.class);
    	dbc = BeanUtils.getBean(DatabaseClient.class);
    	supplierDao = BeanUtils.getBean(SupplierDao.class);
        generatedKey = null;
        
        
        
        btnUploadImage.setOnAction(new EventHandler<ActionEvent>() {
		
		@Override
		public void handle(ActionEvent event) {
			System.out.println("I'm here");
			uploadImage(event);
			
		}
	});
        
  
        if (defPartyTitle != null && !defPartyTitle.trim().isEmpty()) {
            payeeField.setText(defPartyTitle);
            payeeField.setEditable(false);
            if (payeeType != null) {
                payeeField.setUserData(EntityType.getEntityTypeForValue(payeeType));
            }
        }
        if (defaultAmt != null && !defaultAmt.trim().isEmpty()) {
            amountField.setText(String.valueOf(Double.valueOf(defaultAmt).intValue()));
            amountField.setEditable(false);
        }
        if (expenseType != null && !expenseType.trim().isEmpty()) {
            searchExpenseType.setText(expenseType);
            searchExpenseType.setEditable(false);
        }
        DateTimeFormatter formatter = null;

        try {
            String format = DateUtil.determineDateFormat(defDate);
            formatter = DateTimeFormatter.ofPattern(format);
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        LocalDate date = null;
        if (defDate != null && formatter != null) {
            try {
                date = LocalDate.parse(defDate, formatter);
                dateField.setValue(date);
                dateField.setDisable(true);
            } catch (Exception x) {
                dateField.setValue(LocalDate.now());
                System.err.println("Incorrect date format " + x.getMessage());
            }

        }
        if (!Utils.isEmptyString(defComment)) {
            commentField.setText(defComment);
            commentField.setEditable(false);
        }
        cboPaymentType.setItems(FXCollections.observableArrayList(PaymentMethodSource.getValueList()));
        cboPaymentType.setValue(PaymentMethodSource.Cash.toString());
        cboPaymentType.setEditable(false);
        cboPaymentType.setValue(defPayMethodSource.toString());
        if (defPayMethodSource.equals(PaymentMethodSource.Bank)) {
            paneBankDetails.setVisible(true);
            paneBankDetails.setDisable(false);
            if (chequeNo != null) {
                txtChequeNo.setText(chequeNo);
                txtChequeNo.setDisable(true);
            }
            if (defDate != null) {
                dpDepositDate.setValue(date);
                dpDepositDate.setDisable(true);
            }
            if (bankName != null) {
                txtBankName.setText(bankName);
                txtBankName.setDisable(true);
            }
        }
        cboPaymentType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.equals(oldValue) && newValue.equalsIgnoreCase("Bank")) {
                paneBankDetails.setDisable(false);
            } else {
                paneBankDetails.setDisable(true);
            }
        });
        
        
        
        growersList = updateGrowersList();
        payeeField.setEntries(growersList);
        // grower.setLinkedTextFields(new TextField[] { grNo });
        payeeField.setLinkedFieldsReturnType(AutoCompleteTextField.ENTRY_IND);
        payeeField.linkToWindow(ExpenseAddController.this, "/fxml/supplieradd.fxml", "Add new supplier", STR_ADD_NEW,
                new AddSupplierController());
        payeeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue,
                    Boolean newValue) {
            	payeeField.getMenu().hide();
                growersList = updateGrowersList();
                payeeField.setEntries(growersList);
            }
        });
        payeeSearchButton.setPartyType(PartyType.SUPPLIERS);
        payeeSearchButton.setLinkedObject(payeeField);
        
        // Upload Image
        
        TreeSet<String> entriesList = new TreeSet<>(ed.getExpenditureTypeList());
        searchExpenseType.setEntries(entriesList);
        amountField.focusedProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) -> {
            if (aBoolean2) {
            } else if (!amountField.getText().equals("")) {
                try {
                    Integer.parseInt(amountField.getText());
                } catch (NumberFormatException e) {
                    GeneralMethods.errorMsg("Value must be an integer!");
                    amountField.setText("");
                }
            }
        });
        payeeSearchButton.setPartyType(PartyType.BUYER_SUPPLIERS);
        payeeSearchButton.setLinkedObject(payeeField);

        create.setOnAction((ActionEvent event) -> {
            if (!areFieldsValid()) {
                return;
            }
            saveExpenditure();
            create.getScene().getWindow().hide();
        });
    }
 
    private java.util.TreeSet<String> updateGrowersList() {
        int rowsNum = dbc.getRowsNum("suppliers1");
        java.util.TreeSet<String> result = new java.util.TreeSet<>();
        
        for (int supp_id = 1; supp_id <= rowsNum; supp_id++) {
            try {
                Supplier supplier = supplierDao.getSupplierById(supp_id);
                if (supplier != null )
                	result.add(supplier.getTitle());
            }
            catch (java.sql.SQLException e) {
                System.out.print("sqlexception in populating suppliers list");
            }
        }
        if (result.isEmpty()) {
            result.add(STR_ADD_NEW);
        }
        return result;
        }
    

    public ExpenseAddController(String defaultAmt, String defPartyTitle,
            String expenseType, String payeeType, String defDate, String chequeNo,
            String bankName, String defComment, PaymentMethodSource paymentMethod) {
        this.defaultAmt = defaultAmt;
        this.defPartyTitle = defPartyTitle;
        this.expenseType = expenseType;
        this.payeeType = payeeType;
        this.defDate = defDate;
        this.defComment = defComment;
        this.chequeNo = chequeNo;
        this.bankName = bankName;
        this.defPayMethodSource = paymentMethod;
    }
    
    public ExpenseAddController() {
        this.defPayMethodSource = PaymentMethodSource.Cash;
    }
    private void saveExpenditure() {
        generatedKey = null;
        Expenditure xpr = new Expenditure();
        xpr.setAmount(amountField.getText());
        xpr.setDate(dateField.getValue().toString());
        xpr.setComment(commentField.getText());
        xpr.setPayee(payeeField.getText());
        xpr.setType(searchExpenseType.getText());
        if (imgFile != null && imgFile.exists()) {
            try {
                xpr.setReceipt(new FileInputStream(imgFile));
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        MoneyPaidRecd mpr = new MoneyPaidRecd();
        String partyType = ((EntityType) payeeField.getUserData()).getValue();
        mpr.setDate(dateField.getValue().toString());
        mpr.setIsAdvanced(Boolean.FALSE.toString());
        mpr.setPartyType(partyType);
        mpr.setPaymentMode(PaymentMethodSource.Bank.toString());
        mpr.setReceived(amountField.getText().trim());
        mpr.setPaid("0");
        mpr.setTitle(payeeField.getText().trim());
        mpr.setDescription(searchExpenseType.getText());
        String title = "Expense entry recorded in supplier ledger";
        if (cboPaymentType.getValue().equalsIgnoreCase("bank")) {
            mpr.setBankName(txtBankName.getText());
            mpr.setChequeNo(txtChequeNo.getText());
            mpr.setDepositDate(dpDepositDate.getValue().toString());
            title = "Assigned expense entry from bank account";
        }
        mpr.setPaymentMode(cboPaymentType.getValue());
        // save transaction
        if (ed.addExpenditure(xpr)) {
            generatedKey = mpd.addMoneyPaidRecdInfo(mpr, title);
        }
    }

    private boolean areFieldsValid() {
        boolean result = true;
        try {
            Double.valueOf(amountField.getText());
        } catch (Exception x) {
            GeneralMethods.errorMsg("Please enter correct number in amount field");
            result = false;
        }
        if (payeeField.getText().trim().isEmpty()) {
            GeneralMethods.errorMsg("Please enter payee info");
            result = false;
        }
        if (searchExpenseType.getText().trim().isEmpty()) {
            GeneralMethods.errorMsg("Please enter expense type info");
            result = false;
        }
        return result;
    }

   
    public void uploadImage(final Event event) {
    	System.out.println("image");;
    	if (!(event.getSource() instanceof Node)) 
    		throw new IllegalArgumentException("The source of the event should be instance of java FX node or any of its' subclass");
 		Window mainStage =  ((Node) event.getSource()).getScene().getWindow();
 		FileChooser fileChooser = new FileChooser();
 		 fileChooser.setTitle("Open Resource File");
 		 fileChooser.getExtensionFilters().addAll( new ExtensionFilter
 		         ("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
 		 File selectedFile = fileChooser.showOpenDialog(mainStage);
 		 if (selectedFile != null) {
 		    try (InputStream is = new FileInputStream(selectedFile)){
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
