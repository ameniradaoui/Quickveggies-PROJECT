package com.quickveggies.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.ai.util.dates.DateUtil;
import com.quickveggies.BeanUtils;
import com.quickveggies.GeneralMethods;
import com.quickveggies.Main;
import com.quickveggies.PaymentMethodSource;
import com.quickveggies.dao.DatabaseClient;
import com.quickveggies.dao.ExpenditureDao;

import com.quickveggies.dao.MoneyPaidRecdDao;
import com.quickveggies.dao.SupplierDao;
import com.quickveggies.dao.UserUtils;
import com.quickveggies.entities.ArrivalSelectionFilter;
import com.quickveggies.entities.Expenditure;
import com.quickveggies.entities.ExpenditureType;
import com.quickveggies.entities.ExpenditureTypeName;
import com.quickveggies.entities.Journal;
import com.quickveggies.entities.MoneyPaidRecd;
import com.quickveggies.entities.PartyType;
import com.quickveggies.entities.Supplier;
import com.quickveggies.entities.User;
import com.quickveggies.impl.IExpenditureDao;
import com.quickveggies.impl.IMoneyPaidRecordDao;
import com.quickveggies.misc.AutoCompleteTextField;
import com.quickveggies.misc.CryptDataHandler;
import com.quickveggies.misc.SearchPartyButton;
import com.quickveggies.misc.Utils;
import com.quickveggies.model.DaoGeneratedKey;
import com.quickveggies.model.EntityType;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class ExpenditureAddController implements Initializable {
	
	 @FXML
	    private Button billBut;

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
    private ComboBox<String> expenseTypefilter;
    
    @FXML
    private AutoCompleteTextField payeeField;

    @FXML
    private Button create;

    private File imgFile;
    
    private Long generatedKey = null;
 
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
        
        LocalDate date = LocalDate.now();
        dateField.setValue(date);
        amountField.setId("txtinput");
        commentField.setId("txtinput");
      
        List<ExpenditureTypeName> selectionFiler = ed.getSelectionFiler();
    	  List<String> list = new ArrayList<String>();
    	for (ExpenditureTypeName arrivalSelectionFilter : selectionFiler) {
  			list.add(arrivalSelectionFilter.getName());
  		}
         ObservableList obList = FXCollections.observableList(list);
         expenseTypefilter.getItems().clear();
         expenseTypefilter.setItems(obList);
         
         BackgroundImage backgroundImage = new BackgroundImage(
                 new Image(getClass().getResource("/icons/deleteIcon.png").toExternalForm(), 30, 30, true, true),
                 BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                 BackgroundSize.DEFAULT);
         Background background = new Background(backgroundImage);
     
         payeeSearchButton.setPartyType(PartyType.SUPPLIERS);
         payeeSearchButton.setLinkedObject(payeeField);
        
        create.setOnAction((ActionEvent event) -> {
	           
	         
		
				
					saveMprObject();
					
			
		
			create.getScene().getWindow().hide();
    });

        
        btnUploadImage.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				//System.out.println("I'm here");
				uploadImage(event);
				
			}
		});
       
              
        
    }



	



	private void saveMprObject()  {
		generatedKey = null;
//		System.out.println("size" +journalList.size());
//		for (Journal journal:journalList) {
			
			Expenditure mpr = new Expenditure();
			mpr.setAmount(amountField.getText());
			mpr.setComment(commentField.getText());
			mpr.setType(expenseTypefilter.getValue());
			mpr.setDate(dateField.getValue().toString());
			mpr.setPayee(payeeField.getText());
			
			
//			if (imgFile != null && imgFile.exists()) {
//					
//							try {
//								mpr.setReceipt(new FileInputStream(imgFile));
//							} catch (Exception e) {
//								e.printStackTrace();
//								throw e;
//							}
//				
//			}

			generatedKey = ed.addExpenditure(mpr);
			
		
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
