package com.quickveggies.controller.dashboard;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.quickveggies.BeanUtils;
import com.quickveggies.controller.MoneyPaidRecdController;
import com.quickveggies.controller.SessionDataController;
import com.quickveggies.dao.CompanyDao;
import com.quickveggies.entities.Company;


import javafx.scene.control.ProgressBar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class IntroDashController implements Initializable {

	
	@FXML
	private ProgressBar progressbar1;
	
	@FXML
	private Button buttoncompany;
    @FXML
    private Label lblPendingLadaan;

    @FXML
    private Label lblPendingColdStore;

    @FXML
    private Label lblPendingGodown;

    @FXML
    private ImageView imgCompanyLogo;

    @FXML
    private Label lblCompanyName;

    @FXML
    private Label dashDate;

    private DashboardController dashboard;

    private Company company;

    private CompanyDao cd ;
    
    
    private SessionDataController session = SessionDataController.getInstance();
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	cd = BeanUtils.getBean(CompanyDao.class);
        
        LocalDateTime date = LocalDateTime.now();
        dashDate.setText("" + date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", "
                + date.getDayOfMonth() + ", " + date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", "
                + date.getYear());

        Label[] labels = new Label[]{lblPendingColdStore, lblPendingGodown, lblPendingLadaan};
        lblPendingLadaan.textProperty().bindBidirectional(session.pendingLadaanEntriesProp);
        lblPendingColdStore.textProperty().bindBidirectional(session.pendingColdStoreEntriesProp);
        lblPendingGodown.textProperty().bindBidirectional(session.pendingGodownEntriesProp);
        for (final Label label : labels) {
            label.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    int pendingCount = Integer.valueOf(newValue);
                    if (pendingCount == 0) {
                        label.setTextFill(Color.GREEN);
                    } else if (pendingCount > 0) {
                        label.setTextFill(Color.HOTPINK);
                    }
                }
            });
            label.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY)));
            addHoverEffectsToControl(label);
            label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == null) {
                        return;
                    }
                    if (event.getButton() != MouseButton.PRIMARY) {
                        return;
                    }

                    switch (label.getId()) {
                        case "lblPendingLadaan":
                            dashboard.fireLadaan();
                            break;
                        case "lblPendingGodown":
                            dashboard.fireGodown();
                            break;
                        case "lblPendingColdStore":
                            dashboard.fireColdstore();
                            break;
                    }
                }
            });
        }
//        if (company.getLogo() != null) {
//            Image companyImage = new Image(company.getLogo());
//            imgCompanyLogo.setImage(companyImage);
//        }
//        if (company.getName() != null) {
//            lblCompanyName.setText(company.getName());
//        }
        lblPendingLadaan.setTooltip(new Tooltip("Click to open Ladaan/Bijak Dashboard"));
        session.resetPendingLadaanEntries();
        session.resetPendingColdStoreEntries();
        session.resetPendingGodownEntries();
        
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResource("/icons/add.png").toExternalForm(), 50, 50, true, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
    
        buttoncompany.setBackground(background);
        
        
        buttoncompany.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	//new Main().replaceSceneContent("/fxml/glCodes.fxml");
            	
            	
				try {
					 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/companyadd.fxml"));
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
        
        
        //here we will create the formulaire 
        progressbar1.setProgress(0.9);
    
       
        
            
        
    }

    public IntroDashController(DashboardController dashboard) {
    	
        this.dashboard = dashboard;
        if (company == null) {
            company = new Company();
            try {
                InputStream is = new BufferedInputStream(
                        IntroDashController.class.getResourceAsStream("/icons/logo.png"));
                company.setLogo(is);
            } catch (Exception x) {
                x.printStackTrace();
            }
        }

    }

  

    private void addHoverEffectsToControl(Control control) {
        final ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0.0);
        control.setEffect(colorAdjust);
    }

}
