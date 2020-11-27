package Controllers;

import Entities.Doctor;
import Entities.LinkedKey;
import Entities.Patient;
import Entities.PatientCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.util.Callback;
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

public class NewPatient implements Initializable {
    private final static String SURNAME_PATTERN = "[А-ЯЁ][а-яё]++(?:-[А-ЯЁ][а-яё]++)?";
    private final static String NAME_PATTERN = "[А-ЯЁ][а-яё]++";
    private final static String PATRONYMIC_PATTERN = "[А-ЯЁ][а-яё]++";

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session;

    @FXML private TextField surnameField;
    @FXML private TextField loginField;
    @FXML private TextField passwordField;
    @FXML private TextField homeAddressField;
    @FXML private TextField nameField;
    @FXML private TextField patronymicField;
    @FXML private DatePicker birthDayField;
    @FXML private ComboBox<Doctor> doctorMenu;
    @FXML private DatePicker initialDateField;

    boolean admin;
    List<?> admLog;
    List<?> recLog;
    List<?> docLog;
    List<?> patLog;
    ObservableList<Doctor> doctors;

    public NewPatient(boolean admin){
        this.admin = admin;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        session = sessionFactory.openSession();
        doctors = FXCollections.observableArrayList(
                session.createQuery("from Doctor", Doctor.class).getResultList());
        session.close();
        doctorMenu.setItems(doctors);
        doctorMenu.getSelectionModel().selectFirst();

        Callback<ListView<Doctor>, ListCell<Doctor>> cellCallback = new Callback<>() {
            @Override
            public ListCell<Doctor> call(ListView<Doctor> doctorListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Doctor doctor, boolean empty) {
                        super.updateItem(doctor, empty);
                        if (doctor == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(doctor.getSurname() + ": " + doctor.getSpeciality());
                        }
                    }
                };
            }
        };
        doctorMenu.setButtonCell(cellCallback.call(null));
        doctorMenu.setCellFactory(cellCallback);
    }

    @FXML
    void cancel(ActionEvent event) throws IOException {
        if(!admin) {
            changeScene(event, "/receptionistPanel", "Receptionist Panel");
        }
        else{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void save(ActionEvent event) throws IOException {

        String surname = surnameField.getText();
        String name = nameField.getText();
        String patronymic = patronymicField.getText();
        LocalDate birthday = birthDayField.getValue();
        LocalDate initialDate = initialDateField.getValue();
        String homeAddress = homeAddressField.getText();
        String login = loginField.getText();
        String password = passwordField.getText();
        Doctor doc = doctorMenu.getValue();

        if(!checkCorrect(surname, name, patronymic,
                birthday, homeAddress,initialDate,
                login, password, doc)){
            return;
        }

        Patient patient = new Patient(login, password, surname, name, patronymic, birthday, homeAddress);
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(patient);
            session.getTransaction().commit();
        }
        catch (Exception exception){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }

        String docFIO = doc.getSurname() + " " + doc.getName() + " " + doc.getPatronymic();
        String patFIO = surname + " " + name + " " + patronymic;
        PatientCard card = new PatientCard(doc.getId(), patient.getId(), initialDate, docFIO, patFIO);
        LinkedKey key = new LinkedKey(patient.getId(), doc.getId(), initialDate);
        card.setKey(key);

        try{
            session.beginTransaction();
            session.save(card);
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

    private Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    private void changeScene(ActionEvent event, String panel, String title) throws IOException {
        Scene scene = new Scene(loadFXML(panel));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private boolean checkCorrect(String surname, String name, String patronymic,
                                 LocalDate birthday, String home, LocalDate initialDate,
                                 String login, String password, Doctor doctor){

        boolean correct = true;

        session = sessionFactory.openSession();
        try {
            admLog = session.createSQLQuery("select login from Admins").list();
            recLog = session.createSQLQuery("select login from Receptionists").list();
            docLog = session.createSQLQuery("select login from Doctors").list();
            patLog = session.createSQLQuery("select login from Patients").list();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        session.close();

        if(!surname.matches(SURNAME_PATTERN) || surname.isEmpty()){
            surnameField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            surnameField.setStyle("-fx-control-inner-background: white;");
        }

        if(!name.matches(NAME_PATTERN) || name.isEmpty()){
            nameField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            nameField.setStyle("-fx-control-inner-background: white;");
        }

        if(!patronymic.matches(PATRONYMIC_PATTERN) || patronymic.isEmpty()){
            patronymicField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else {
            patronymicField.setStyle("-fx-control-inner-background: white;");
        }

        if(birthday == null || birthday.isAfter(LocalDate.now())){
            birthDayField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            birthDayField.setStyle("-fx-control-inner-background: white;");
        }

        if(home.isEmpty()){
            homeAddressField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            homeAddressField.setStyle("-fx-control-inner-background: white;");
        }

        if(doctor == null){
            doctorMenu.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            doctorMenu.setStyle("-fx-control-inner-background: white;");
        }

        if(initialDate == null || initialDate.isBefore(LocalDate.now())){
            initialDateField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else {
            initialDateField.setStyle("-fx-control-inner-background: white;");
        }

        if(login.isEmpty() || admLog.contains(login) || recLog.contains(login)
                || docLog.contains(login) || patLog.contains(login)){
            loginField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            loginField.setStyle("-fx-control-inner-background: white;");
        }

        if(password.isEmpty()){
            passwordField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            passwordField.setStyle("-fx-control-inner-background: white;");
        }

        return correct;
    }
}
