package Controllers;

import Entities.Doctor;
import Entities.PatientCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DoctorPanel implements Initializable {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session;

    Doctor doctor;
    ObservableList<PatientCard> cards;

    public DoctorPanel(Doctor doctor){
        this.doctor = doctor;
    }

    @FXML private TableView<PatientCard> table;
    @FXML private TableColumn<PatientCard, String> patientFioColumn;
    @FXML private TableColumn<PatientCard, LocalDate> initialDateColumn;
    @FXML private TableColumn<PatientCard, LocalDate> invoiceDateColumn;
    @FXML private TableColumn<PatientCard, Integer> idColumn;
    @FXML private TableColumn<PatientPanel, Integer> docIdColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        session = sessionFactory.openSession();
        cards = FXCollections.observableArrayList(
                session.createQuery("from PatientCard where doctor_id = :doctor_id " +
                        "and initial_date = :initial_date")
                        .setParameter("doctor_id", doctor.getId())
                        .setParameter("initial_date", LocalDate.now())
                        .getResultList()
        );
        session.close();

        patientFioColumn.setCellValueFactory(new PropertyValueFactory<>("patFIO"));
        initialDateColumn.setCellValueFactory(new PropertyValueFactory<>("initialDate"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        docIdColumn.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        table.setItems(cards);
    }

    @FXML
    void changeUser(ActionEvent event) throws IOException {
        changeScene(event, "/enterPanel", "Enter Panel");
    }

    @FXML
    void takePatient(ActionEvent event) throws IOException {
        PatientCard card = table.getSelectionModel().getSelectedItem();
        Card cardPanel = new Card(card, true);
        createWindow(cardPanel);
        cards.removeAll(card);
        table.refresh();
    }
    private void createWindow(Card card) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/card" + ".fxml"));
        fxmlLoader.setController(card);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("New Card");
        stage.setScene(scene);
        stage.show();
    }

    private void changeScene(ActionEvent event, String panel, String title) throws IOException {
        Scene scene = new Scene(loadFXML(panel));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

}
