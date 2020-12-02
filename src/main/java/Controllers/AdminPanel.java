package Controllers;

import Entities.Doctor;
import Entities.Patient;
import Entities.Receptionist;
import Entities.Speciality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.util.List;
import java.util.ResourceBundle;

public class AdminPanel implements Initializable {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session;

    ObservableList<Receptionist> receptionists;
    FilteredList<Receptionist> filteredRec;
    SortedList<Receptionist> sortedRec;

    ObservableList<Doctor> doctors;
    FilteredList<Doctor> filteredDoc;
    SortedList<Doctor> sortedDoc;

    ObservableList<Patient> patients;
    FilteredList<Patient> filteredPat;
    SortedList<Patient> sortedPat;

    @FXML private TextField recSearchField;
    @FXML private TableView<Receptionist> recTable;
    @FXML private TableColumn<Receptionist, String> recSurnameColumn;
    @FXML private TableColumn<Receptionist, String> recNameColumn;
    @FXML private TableColumn<Receptionist, String> recLoginColumn;
    @FXML private TableColumn<Receptionist, String> recPasswordColumn;

    @FXML private TextField docSearchField;
    @FXML private TableView<Doctor> docTable;
    @FXML private TableColumn<Doctor, String> docSurnameColumn;
    @FXML private TableColumn<Doctor, String> docNameColumn;
    @FXML private TableColumn<Doctor, String> docPatronymicColumn;
    @FXML private TableColumn<Doctor, Speciality> docSpecialityColumn;
    @FXML private TableColumn<Doctor, LocalDate> docEmpDateColumn;
    @FXML private TableColumn<Doctor, String> docLoginColumn;
    @FXML private TableColumn<Doctor, String> docPasswordColumn;

    @FXML private TextField patSearchField;
    @FXML private TableView<Patient> patTable;
    @FXML private TableColumn<Patient, String> patSurnameColumn;
    @FXML private TableColumn<Patient, String> patNameColumn;
    @FXML private TableColumn<Patient, String> patPatronymicColumn;
    @FXML private TableColumn<Patient, Integer> patIdColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        recNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        recLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        recPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        docSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        docNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        docPatronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        docSpecialityColumn.setCellValueFactory(new PropertyValueFactory<>("speciality"));
        docEmpDateColumn.setCellValueFactory(new PropertyValueFactory<>("hiringDate"));
        docLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        docPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        patSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patPatronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        patIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    }

    /*----------------------------------*/
    @FXML
    void recTabOpen(Event event) {
        session = sessionFactory.openSession();
        receptionists = FXCollections.observableArrayList(
                session.createQuery("from Receptionist", Receptionist.class).getResultList());
        session.close();
        filteredRec = new FilteredList<>(receptionists, b -> true);
        sortedRec = new SortedList<>(filteredRec);
        sortedRec.comparatorProperty().bind(recTable.comparatorProperty());
        recTable.setItems(sortedRec);
        recTable.refresh();

        recSearchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredRec.setPredicate(receptionist -> {
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowercaseFilter = newValue.toLowerCase();

                    if(receptionist.getSurname().toLowerCase().contains(lowercaseFilter)){
                        return true;
                    }
                    else return receptionist.getName().toLowerCase().contains(lowercaseFilter);
                }));

    }

    @FXML
    void employRec(ActionEvent event) throws IOException {
        createWindow("/empReceptionistPanel", "New Receptionist");
    }

    @FXML
    void dismissRec(ActionEvent event) {
        Receptionist receptionist = recTable.getSelectionModel().getSelectedItem();
        session = sessionFactory.openSession();
        receptionists.removeAll(receptionist);
        try {
            session.beginTransaction();
            session.delete(receptionist);
            session.getTransaction().commit();
        }
        catch (Exception ex){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();
    }

    @FXML
    void recRefresh(ActionEvent event) {
        session = sessionFactory.openSession();
        receptionists = FXCollections.observableArrayList(
                session.createQuery("from Receptionist", Receptionist.class).getResultList());
        session.close();
        filteredRec = new FilteredList<>(receptionists, b -> true);
        sortedRec = new SortedList<>(filteredRec);
        sortedRec.comparatorProperty().bind(recTable.comparatorProperty());
        recTable.setItems(sortedRec);
        recTable.refresh();
    }

    /*-----------------------------------*/

    @FXML
    void docTabOpen(Event event) {
        session = sessionFactory.openSession();
        doctors = FXCollections.observableArrayList(
                session.createQuery("from Doctor", Doctor.class).getResultList());
        session.close();
        filteredDoc = new FilteredList<>(doctors, b -> true);
        sortedDoc = new SortedList<>(filteredDoc);
        sortedDoc.comparatorProperty().bind(docTable.comparatorProperty());
        docTable.setItems(sortedDoc);
        docTable.refresh();

        docSearchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredDoc.setPredicate(doctor -> {
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowercaseFilter = newValue.toLowerCase();

                    if(doctor.getSurname().toLowerCase().contains(lowercaseFilter)){
                        return true;
                    }
                    else if(doctor.getName().toLowerCase().contains(lowercaseFilter)){
                        return true;
                    }
                    else return doctor.getPatronymic().toLowerCase().contains(lowercaseFilter);
        }));
    }

    @FXML
    void employDoc(ActionEvent event) throws IOException {
        createWindow("/empDoctorPanel", "New Doctor");
    }

    @FXML
    void dismissDoc(ActionEvent event) {
        Doctor doctor = docTable.getSelectionModel().getSelectedItem();
        session = sessionFactory.openSession();
        doctors.removeAll(doctor);
        try {
            session.beginTransaction();
            session.delete(doctor);
            session.getTransaction().commit();
        }
        catch (Exception ex){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();
    }

    @FXML
    void docRefresh(ActionEvent event) {
        session = sessionFactory.openSession();
        doctors = FXCollections.observableArrayList(
                session.createQuery("from Doctor", Doctor.class).getResultList());
        session.close();
        filteredDoc = new FilteredList<>(doctors, b -> true);
        sortedDoc = new SortedList<>(filteredDoc);
        sortedDoc.comparatorProperty().bind(docTable.comparatorProperty());
        docTable.setItems(sortedDoc);
        docTable.refresh();
    }

    /*------------------------------------*/

    @FXML
    void patTabOpen(Event event) {
        session = sessionFactory.openSession();
        patients = FXCollections.observableArrayList(
                session.createQuery("from Patient", Patient.class).getResultList());
        session.close();
        filteredPat = new FilteredList<>(patients, b -> true);
        sortedPat = new SortedList<>(filteredPat);
        sortedPat.comparatorProperty().bind(patTable.comparatorProperty());
        patTable.setItems(sortedPat);
        patTable.refresh();

        patSearchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredPat.setPredicate(patient -> {
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
    }

    @FXML
    void addNewPat(ActionEvent event) throws IOException {
        createWindow(new NewPatient(true));
    }

    @FXML
    void deletePat(ActionEvent event) {
        Patient patient = patTable.getSelectionModel().getSelectedItem();
        session = sessionFactory.openSession();
        List<?> cards = session.createQuery("from PatientCard where patient_id=:patient_id")
                .setParameter("patient_id", patient.getId()).getResultList();
        if(!cards.isEmpty()) {
            cards.forEach(card -> {
                try {
                    session.beginTransaction();
                    session.delete(card);
                    session.getTransaction().commit();
                } catch (Exception ex) {
                    session.getTransaction().rollback();
                    System.out.println("The transaction was not completed");
                }
            });
        }
        patients.removeAll(patient);
        try {
            session.beginTransaction();
            session.delete(patient);
            session.getTransaction().commit();
        }
        catch (Exception ex){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();
    }

    @FXML
    void detailsPat(ActionEvent event) {
        Patient patient = patTable.getSelectionModel().getSelectedItem();
        PatientPanel patientPanel = new PatientPanel(patient, false, true);
        try {
            createWindow(patientPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void patRefresh(ActionEvent event) {
        session = sessionFactory.openSession();
        patients = FXCollections.observableArrayList(
                session.createQuery("from Patient", Patient.class).getResultList());
        session.close();
        filteredPat = new FilteredList<>(patients, b -> true);
        sortedPat = new SortedList<>(filteredPat);
        sortedPat.comparatorProperty().bind(patTable.comparatorProperty());
        patTable.setItems(sortedPat);
        patTable.refresh();
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

    private void createWindow(NewPatient newPatient) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/newPatientPanel" + ".fxml"));
        fxmlLoader.setController(newPatient);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("New Patient");
        stage.setScene(scene);
        stage.show();
    }
    /*-------------------------------------*/

    @FXML
    void changeUser(ActionEvent event) throws IOException {
        changeScene(event, "/enterPanel", "Enter Panel");
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
    /*-------------------------------------*/

    private void createWindow(String panel, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(panel + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
