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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import com.ai.util.dates.DateUtil;
import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.Main;
import com.quickveggies.PaymentMethodSource;
import com.quickveggies.dao.BuyerDao;
import com.quickveggies.dao.DatabaseClient;
import com.quickveggies.dao.MoneyPaidRecdDao;
import com.quickveggies.dao.SupplierDao;
import com.quickveggies.entities.Buyer;
import com.quickveggies.entities.GLCode;
import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.entities.PartyType;
import com.quickveggies.entities.Supplier;
import com.quickveggies.misc.AutoCompleteTextField;
import com.quickveggies.misc.SearchPartyButton;
import com.quickveggies.model.DaoGeneratedKey;
import com.quickveggies.model.EntityType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MoneyPaidRecdController implements Initializable, DaoGeneratedKey {

    public MoneyPaidRecdController() {
		super();
		// TODO Auto-generated constructor stub
	}

	public enum AmountType {
        PAID, RECEIVED;
    }
	
	
	private static GLCode glcodeSelected = new GLCode();

    private static final String STR_ADD_NEW = "Add new...";

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblPartyType;

    @FXML
    private AutoCompleteTextField txtParty;

    @FXML
    private TextField txtAmount;

    @FXML
    private ComboBox<String> cboPaymentType;

    @FXML
    private Button btnSave;
    
    @FXML
    private Button glTag;

    @FXML
    private SearchPartyButton btnSearchParty;

    private static final String EMPTY_STR = "";

    private EntityType partType;

    private AmountType amountType;

    private TreeSet<String> partyList;

    @FXML
    private DatePicker dpDate;

    @FXML
    private DatePicker dpDepositDate;

    @FXML
    private CheckBox chkAdvanced;

    @FXML
    private TextField txtChequeNo;

    @FXML
    private TextField txtBankName;

    @FXML
    private  TextField txtgl;
    
  
    private static String name ="";
	

	@FXML
    private Button btnAddSnapshot;

    @FXML
    private Pane paneBankDetails;

    private InputStream receiptImageStream;

    private boolean isAdvanced = false;
    private Long generatedKey = null;
    
    
    private BuyerDao bd ;
    
    private SupplierDao supplierDao;
    
    private MoneyPaidRecdDao mpd;
   
    private DatabaseClient dbclient ;

    private final BooleanProperty advancedPayProperty = new SimpleBooleanProperty();

    public static final String PAID = "Paid to (Dr.)";

    public static final String RECEIVED = "Received from (Cr.)";

    private PaymentMethodSource defPayMethodSource;
    private String defaultAmt, defPartyTitle, defDate, chequeNo, bankName;

	private String nameOfLedger = "default";

    public MoneyPaidRecdController(EntityType partyType, AmountType amountType) {
        this.partType = partyType;
        this.amountType = amountType;
       
    }

    public MoneyPaidRecdController(EntityType partyType, AmountType amountType, boolean isAdvanced) {
        this.partType = partyType;
        this.amountType = amountType;
        this.isAdvanced = isAdvanced;
        advancedPayProperty.set(isAdvanced);
    }

    public MoneyPaidRecdController(EntityType partyType, AmountType amountType, boolean isAdvanced, String defaultAmt,
            PaymentMethodSource defPayMethodSource, String defPartyTitle, String defDate, String chequeNo, String bankName) {
        this.partType = partyType;
        this.amountType = amountType;
        this.isAdvanced = isAdvanced;
        advancedPayProperty.set(isAdvanced);
        this.defaultAmt = defaultAmt;
        this.defPayMethodSource = defPayMethodSource;
        this.defPartyTitle = defPartyTitle;
        this.defDate = defDate;
        this.chequeNo = chequeNo;
        this.bankName = bankName;
    }
    
    
    @SuppressWarnings("restriction")
	public void initData (String name){
    	this.name  = name;
    }
    
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
    	
        bd = BeanUtils.getBean(BuyerDao.class);
        supplierDao = BeanUtils.getBean(SupplierDao.class);
        mpd = BeanUtils.getBean(MoneyPaidRecdDao.class);
        dbclient = BeanUtils.getBean(DatabaseClient.class);
       
        
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResource("/icons/search_icon.png").toExternalForm(), 30, 30, true, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
    
        glTag.setBackground(background);
        
       
        
        
        generatedKey = null;
        LocalDate date = LocalDate.now();
        chkAdvanced.visibleProperty().bindBidirectional(advancedPayProperty);
        chkAdvanced.setSelected(isAdvanced);

        if (this.partType == null) {
            throw new IllegalStateException("Party type must be set before calling this method");
        }
        if (this.amountType == null) {
            throw new IllegalStateException("Amount type must be set before calling this method");
        }
        switch (amountType) {
        
            case PAID:
                lblTitle.setText("Money Paid");
                break;
            case RECEIVED:
                lblTitle.setText("Money Received");
                chkAdvanced.setVisible(false);
                break;
        }
        lblPartyType.setText(partType.getValue().concat(" Title"));
        dpDate.setValue(date);
        if  (glcodeSelected.getNameOfLedger() != null)
        	txtgl.setText(glcodeSelected.getNameOfLedger());
//        partyList = updatePartyList(partType);
//        txtParty.setEntries(partyList);
//        txtParty.setLinkedFieldsReturnType(AutoCompleteTextField.ENTRY_IND);
//        switch (partType) {
//            case BUYER:
//            case LADAAN:
//            case BIJAK:
//                txtParty.linkToWindow(this, "/fxml/buyeradd.fxml", "Add new Buyer",
//                        STR_ADD_NEW, new AddBuyerController());
//                break;
//            case SUPPLIER:
//                txtParty.linkToWindow(this, "/fxml/supplieradd.fxml", "Add new supplier",
//                        STR_ADD_NEW, new AddSupplierController());
//        }
//        txtParty.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue,
//                    Boolean newValue) {
//                if (txtParty.getMenu().isShowing()) {
//                    txtParty.getMenu().hide();
//                }
//                if (newValue) {
//                    partyList = updatePartyList(MoneyPaidRecdController.this.partType);
//                    txtParty.setEntries(partyList);
//                }
//            }
//        });
        txtParty.setPromptText("Enter ".concat(this.partType.getValue()).concat(" here"));
        cboPaymentType.setItems(FXCollections.observableArrayList(PaymentMethodSource.getValueList()));
        cboPaymentType.setValue(PaymentMethodSource.Cash.toString());
        cboPaymentType.setEditable(false);

        if (defPartyTitle != null && !defPartyTitle.trim().isEmpty()) {
            txtParty.setText(defPartyTitle);
            txtParty.setEditable(false);
        }

        if (defaultAmt != null && !defaultAmt.trim().isEmpty()) {
            txtAmount.setText(defaultAmt);
            txtAmount.setEditable(false);
        }

        DateTimeFormatter formatter = null;

        try {
            String format = DateUtil.determineDateFormat(defDate);
            formatter = DateTimeFormatter.ofPattern(format);
        } catch (Exception x) {
            x.printStackTrace();
        }
        if (defDate != null && formatter != null) {
            try {
                date = LocalDate.parse(defDate, formatter);
                dpDate.setValue(date);
                dpDate.setDisable(true);
            } catch (Exception x) {
                System.err.println("Incorrect date format " + x.getMessage());
            }
        }
        if (defPayMethodSource != null) {
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
                    dpDepositDate.setDisable(true);
                }
            }
        }
        cboPaymentType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.equals(oldValue) && newValue.equalsIgnoreCase("Bank")) {
                paneBankDetails.setDisable(false);
            } else {
                paneBankDetails.setDisable(true);
            }
        });
        btnSave.setOnAction((ActionEvent event) -> {
            if (!areFieldsValid()) {
                return;
            }
            saveMprObject();
            btnSave.getScene().getWindow().hide();
        });
        txtAmount.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (txtAmount.isFocused() && txtParty.getText().trim().isEmpty()) {
                txtParty.requestFocus();
            }
        });
        txtParty.setOnAction((ActionEvent event) -> {
            String text = txtParty.getText().trim();
            if (partyList.isEmpty()) {
                // stop event propagation here
                event.consume();
                return;
            }
            if (!partyList.contains(text)) {
                GeneralMethods.errorMsg("Please enter or select the correct property from list");
                event.consume();
                return;
            }
            dpDate.requestFocus();
        });
        dpDate.focusedProperty().addListener(new MyChangeListener<>(dpDate));
        dpDepositDate.focusedProperty().addListener(new MyChangeListener<>(dpDepositDate));

        dpDate.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (dpDate.isShowing()) {
                    dpDate.hide();
                } else {
                    txtAmount.requestFocus();
                }
            }
        });
        txtAmount.setOnAction((ActionEvent event) -> {
            if (!isAmountValid()) {
                event.consume();
                return;
            }
            cboPaymentType.requestFocus();
        });
        cboPaymentType.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (cboPaymentType.getValue().isEmpty()) {
                    cboPaymentType.setValue(PaymentMethodSource.Cash.toString());
                }
                if (cboPaymentType.getValue().equals(PaymentMethodSource.Bank.toString())) {
                    txtChequeNo.requestFocus();
                } else {
                    btnSave.requestFocus();
                }
            }
        });
        txtChequeNo.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    dpDepositDate.requestFocus();
                }
            }
        });
        dpDepositDate.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (dpDepositDate.isShowing()) {
                        dpDepositDate.hide();
                    } else {
                        txtBankName.requestFocus();
                    }
                }
            }
        });
        txtBankName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    btnAddSnapshot.requestFocus();
                }
            }
        });
        btnAddSnapshot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                uploadImage();
            }
        });
       
		
        glTag.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	//new Main().replaceSceneContent("/fxml/glCodes.fxml");
            	
            	
				try {
					 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/glCodes.fxml"));
	                 Parent root1;
					 root1 = (Parent) fxmlLoader.load();
					 Stage stage = new Stage();
	                 stage.setScene(new Scene(root1));  
	                 stage.show();
	                 
	                stage.setOnHiding( e -> { txtgl.setText(MoneyPaidRecdController.name); } );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 
            }    
                    
    		});
            
       
        
        partyList = updateGrowersList();
        txtParty.setEntries(partyList);
        // grower.setLinkedTextFields(new TextField[] { grNo });
        txtParty.setLinkedFieldsReturnType(AutoCompleteTextField.ENTRY_IND);
        txtParty.linkToWindow(MoneyPaidRecdController.this, "/fxml/supplieradd.fxml", "Add new supplier", STR_ADD_NEW,
                new AddSupplierController());
        txtParty.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue,
                    Boolean newValue) {
            	txtParty.getMenu().hide();
            	partyList = updateGrowersList();
                txtParty.setEntries(partyList);
            }
        });
        btnSearchParty.setPartyType(PartyType.SUPPLIERS);
        btnSearchParty.setLinkedObject(txtParty);
       

        
    }
    
    private void focusState(boolean value) {
        if (value) {
            System.out.println("Focus Gained");
        }
        else {
            System.out.println("Focus Lost");
        }
    }
    
    private TreeSet<String> updateGrowersList() {
        int rowsNum = dbclient.getRowsNum("suppliers1");
        long rows = rowsNum;
        java.util.TreeSet<String> result = new java.util.TreeSet<>();
        
        for (long supp_id = 1; supp_id <= rowsNum; supp_id++) {
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
    
    @Override
    public Long getGeneratedKey() {
        return generatedKey;
    }

    private boolean areFieldsValid() {
        if (!isAmountValid()) {
            txtAmount.requestFocus();
        }
        String partyName = txtParty.getText() == null ? EMPTY_STR : txtParty.getText().trim();
        if (partyName.isEmpty()) {
            GeneralMethods.errorMsg("Please enter a valid party");
            txtParty.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isAmountValid() {
        String amt = ((txtAmount.getText() == null) || txtAmount.getText().trim().isEmpty()) ? "0"
                : txtAmount.getText().trim();
        try {
            Double.parseDouble(amt);
        } catch (NumberFormatException nfe) {
            GeneralMethods.errorMsg("Please enter a valid number in amount field");
            return false;
        }
        return true;
    }

    private TreeSet<String> updatePartyList(EntityType pType) {
       
        int rowsNum = dbclient.getRowsNum(pType.getTableName());
        long rows = rowsNum;
       TreeSet<String> result = new TreeSet<String>();
        for (long partyId = 1; partyId <= rows; partyId++) {
            try {
                String title = "";
                switch (pType) {
                    case BIJAK:
                    case BUYER:
                    	Buyer buyer = bd.getBuyerById(partyId);
                    	 if (buyer != null )
                    	 title = buyer.getTitle();
                         break;
                    case LADAAN:   
                    case SUPPLIER:
                    	Supplier supplier = supplierDao.getSupplierById(partyId);
                    	 if (supplier != null )
                        title = supplier.getTitle();
                }
                result.add(title);
            } catch (java.sql.SQLException e) {
                System.out.print("sqlexception in populating party list");
            }
        }
        result.add(STR_ADD_NEW);
        return result;
    }

    private String getNormalizedValue(String str) {
        if (str == null) {
            return "0";
        }
        try {
            return ((Integer) Double.valueOf(str).intValue()).toString();

        } catch (Exception ex) {
            return "0";
        }
    }

    private void uploadImage() {
        Stage mainStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select payment file");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(mainStage);
        try {
            if (file != null) {
                btnAddSnapshot.setText("1 File attached");
                receiptImageStream = new BufferedInputStream(new FileInputStream(file));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveMprObject() {
        generatedKey = null;
        String amtPaid = getNormalizedValue(txtAmount.getText());
        String amtReceived = amtPaid;
        switch (amountType) {
            case PAID:
                amtReceived = "0";
                break;
            case RECEIVED:
                amtPaid = "0";
        }
        MoneyPaidRecd mpr = new MoneyPaidRecd();
        mpr.setTag(txtgl.getText());
        mpr.setDate(dpDate.getValue().toString());
        if (cboPaymentType.getValue().equalsIgnoreCase("bank")) {
            mpr.setBankName(txtBankName.getText());
            mpr.setChequeNo(txtChequeNo.getText());
            mpr.setDepositDate(dpDepositDate.getValue().toString());
        }
        mpr.setIsAdvanced(String.valueOf(chkAdvanced.isSelected()));
        mpr.setPaid(amtPaid);
        mpr.setPartyType(partType.getValue());
        mpr.setPaymentMode(cboPaymentType.getValue());
        mpr.setReceipt(receiptImageStream);
        mpr.setReceived(amtReceived);
        mpr.setTitle(txtParty.getText().trim());
        if ("bank".equalsIgnoreCase(mpr.getPaymentMode())) {
            generatedKey = mpd.addMoneyPaidRecdInfo(mpr, "Added bank account entry to party ledger");
        }
        else {
            generatedKey = mpd.addMoneyPaidRecdInfo(mpr);
        }
    }

    private class MyChangeListener<T> implements ChangeListener<Boolean> {

        private DatePicker dp;

        public MyChangeListener(DatePicker dp) {
            this.dp = dp;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (dp.getValue() == null) {
                dp.setValue(LocalDate.now());
            }
            if (!dp.isShowing()) {
                dp.show();
            }
        }
    }
    
    public static String[][] buildTableView(MoneyPaidRecd line) {
        List<String[]> table = new ArrayList<>();
        table.add(new String[]{"DATE", "CASES", "DESCRIPTION", "AMOUNT DR.", "AMOUNT CR."});
        String cases = "---";
        String desc = line.getDescription() == null ? "" : line.getDescription();
        if (desc.isEmpty()) {
            if (!ignoreMoneyCash(line.getPaid())) {
                desc = MoneyPaidRecd.MONEY_PAID;
            }
            if (!ignoreMoneyCash(line.getReceived())) {
                desc = MoneyPaidRecd.MONEY_RECEIVED;
            }
        }
        if ("Bank".equalsIgnoreCase(line.getPaymentMode())) {
            desc += " (Bank)";
        }
        desc += " (" + line.getTitle() + ")";
        String[] dealLine = new String[]{line.getDate(), cases,
            desc, line.getPaid(), line.getReceived()};
        table.add(dealLine);
        String[][] invArr = new String[table.size()][5];
        for (int i = 0; i < table.size(); i++) {
            invArr[i] = table.get(i);
        }
        return invArr;
    }
    
    private static boolean ignoreMoneyCash(String value) {
        return (value == null || value.trim().isEmpty() || value.trim().equals("0"));
    }

	
}
