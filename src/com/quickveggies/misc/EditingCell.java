package com.quickveggies.misc;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.beans.property.*;

import com.quickveggies.entities.Journal;

import javafx.beans.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EditingCell extends TableCell<Journal, String> {
	
	
	private TextField textField;
	 
    public EditingCell() {
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText((String) getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
        textField.focusedProperty().addListener(
            (ObservableValue<? extends Boolean> arg0, 
            Boolean arg1, Boolean arg2) -> {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
	
//	 private TextField textField;
//     
//     public EditingCell() {}
//       
//     @Override
//     public void startEdit() {
//           
//         super.startEdit();
//           
//         if (textField == null) {
//             createTextField();
//         }
//           
//         setGraphic(textField);
//         setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//         textField.selectAll();
//         ///textField.setText("ok");
//     }
//       
//     @Override
//     public void cancelEdit() {
//         super.cancelEdit();
//           
//         setText(String.valueOf(getItem()));
//         setContentDisplay(ContentDisplay.TEXT_ONLY);
//     }
//       
//     @Override
//     public void updateItem(String item, boolean empty) {
//    	 System.out.println(item);
//    	
//         super.updateItem(item, empty);
//           
//         if (empty) {
//             setText(null);
//             setGraphic(null);
//         } else {
//             if (isEditing()) {
//                 if (textField != null) {
//                     textField.setPromptText(getString());
//                 }
////                 setGraphic(textField);
////                 setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//             } else {
//            	 setText(getString());
////                 setContentDisplay(ContentDisplay.TEXT_ONLY);
//             }
//         }
//     }
//       
//     private void createTextField() {
//         textField = new TextField();
//         textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
//         textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
//               
//             @Override
//             public void handle(KeyEvent t) {
//                 if (t.getCode() == KeyCode.ENTER) {
//                   commitEdit(textField.getText());
//                 } else if (t.getCode() == KeyCode.ESCAPE) {
//                     cancelEdit();
//                 }
//             }
//         });
//     }
//       
//     private String getString() {
//    	
//         return getItem() == null ? "" : getItem().toString();
//     }
 


