package Controllers;

import Entities.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import java.util.List;
import java.util.ResourceBundle;

public class ReceptionistPanel implements Initializable {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session = sessionFactory.openSession();

    private ObservableList<Patient> patients = FXCollections.observableArrayList(
            session.createQuery("from Patient", Patient.class).getResultList());
    FilteredList<Patient> filteredPatient = new FilteredList<>(patients, b -> true);
    SortedList<Patient> sortedPatient = new SortedList<>(filteredPatient);

    @FXML private TableView<Patient> table;
    @FXML private TableColumn<Patient, String> surnameColumn;
    @FXML private TableColumn<Patient, String> nameColumn;
    @FXML private TableColumn<Patient, String> patronymicColumn;
    @FXML private TableColumn<Patient, Integer> idColumn;
    @FXML private Button dischargeButton;
    @FXML private Button appointmentButton;
    @FXML private Button newPatButton;
    @FXML private TextField searchField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        table.setItems(patients);

        table.setRowFactory(tv -> {
            TableRow<Patient> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Patient rowData = row.getItem();
                    PatientPanel patientPanel = new PatientPanel(rowData, false);
                    try {
                        createWindow(patientPanel);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Double click on: " + rowData.getName());
                }
            });
            return row;
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredPatient.setPredicate(patient -> {
            if(newValue == null || newValue.isEmpty()){
                return true;
            }
            String lowercaseFilter = newValue.toLowerCase();

            if(patient.getSurname().toLowerCase().contains(lowercaseFilter)){
                return true;
            }
            else if(patient.getName().toLowerCase().contains(lowercaseFilter)){
                return true;
            }
            else return patient.getPatronymic().toLowerCase().contains(lowercaseFilter);
        }));
        activateSearch();
    }

    @FXML
    void addNewPatient(ActionEvent event) throws IOException {
        session.close();
        changeScene(event, "/newPatientPanel", "New Patient");
    }

    @FXML
    void appointment(ActionEvent event) throws IOException {
        Patient apPatient = table.getSelectionModel().getSelectedItem();
        createWindow(new NewCard(apPatient.getId()));
    }

    @FXML
    void changeUser(ActionEvent event) throws IOException {
        session.close();
        changeScene(event, "/enterPanel", "Enter Panel");
    }

    @FXML
    void discharge(ActionEvent event) {
        Patient remPatient = table.getSelectionModel().getSelectedItem();
        List<?> cards = session.createQuery("from PatientCard where patient_id=:patient_id")
                            .setParameter("patient_id", remPatient.getId()).getResultList();
        cards.forEach(card -> {
            try {
                session.beginTransaction();
                session.delete(card);
                session.getTransaction().commit();
            }
            catch (Exception ex){
                session.getTransaction().rollback();
                System.out.println("The transaction was not completed");
            }
        });
        patients.removeAll(remPatient);
        try {
            session.beginTransaction();
            session.delete(remPatient);
            session.getTransaction().commit();
        }
        catch (Exception ex){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
    }

    @FXML
    void refresh(ActionEvent event) {
        patients = FXCollections.observableArrayList(
                session.createQuery("from Patient", Patient.class).getResultList());
        table.setItems(patients);
        activateSearch();
    }

    private void createWindow(PatientPanel patientPanel) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/patientPanel" + ".fxml"));
        fxmlLoader.setController(patientPanel);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Patient Panel");
        stage.setScene(scene);
        stage.show();
    }

    private void createWindow(NewCard newCard) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/newCard" + ".fxml"));
        fxmlLoader.setController(newCard);
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

    private void activateSearch(){
        filteredPatient = new FilteredList<>(patients, b -> true);
        sortedPatient = new SortedList<>(filteredPatient);
        sortedPatient.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedPatient);
    }
}
