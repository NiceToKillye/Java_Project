package Controllers;

import Entities.PatientCard;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Card implements Initializable {

    private final PatientCard card;

    public Card(PatientCard card){
        this.card = card;
    }

    @FXML
    private TextField idTextField;

    @FXML
    private TextField diagnosisField;

    @FXML
    private DatePicker initialDateField;

    @FXML
    private DatePicker invoiceDateField;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idTextField.setText(Integer.toString(card.getPatientId()));
        diagnosisField.setText(card.getDiagnosis());

        initialDateField.setValue(card.getInitialDate());
        initialDateField.setDisable(true);
        initialDateField.setOpacity(1.0);

        invoiceDateField.setValue(card.getInvoiceDate());
        invoiceDateField.setDisable(true);
        invoiceDateField.setOpacity(1.0);
    }
}
