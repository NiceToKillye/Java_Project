package Controllers;

import Entities.PatientCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.net.URL;
import java.util.ResourceBundle;

public class Card implements Initializable {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session;

    @FXML private TextField idTextField;
    @FXML private TextField diagnosisField;
    @FXML private DatePicker initialDateField;
    @FXML private DatePicker invoiceDateField;
    @FXML private Button saveButton;
    @FXML private Button revertButton;

    private final PatientCard card;
    private final boolean doctor;

    public Card(PatientCard card, boolean doctor){
        this.card = card;
        this.doctor = doctor;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idTextField.setText(Integer.toString(card.getPatientId()));
        diagnosisField.setText(card.getDiagnosis());
        initialDateField.setValue(card.getInitialDate());
        initialDateField.setDisable(true);
        initialDateField.setOpacity(1.0);
        invoiceDateField.setValue(card.getInvoiceDate());

        if(!doctor){
            saveButton.setDisable(true);
            saveButton.setVisible(false);

            revertButton.setDisable(true);
            revertButton.setVisible(false);

            invoiceDateField.setDisable(true);
            invoiceDateField.setOpacity(1.0);

            diagnosisField.setEditable(false);
        }
    }

    @FXML
    void revert(ActionEvent event) {
        diagnosisField.setText("");
        invoiceDateField.setValue(null);
    }

    @FXML
    void saveChanges(ActionEvent event) {
        card.setDiagnosis(diagnosisField.getText());
        card.setInvoiceDate(invoiceDateField.getValue());

        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(card);
            session.getTransaction().commit();
        }
        catch (Exception exception){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
