package Controllers;

import Entities.Doctor;
import Entities.Speciality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EmpDoctor implements Initializable {
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

    List<?> admLog;
    List<?> recLog;
    List<?> docLog;
    List<?> patLog;

    @FXML private TextField surnameTextField;
    @FXML private TextField nameTextField;
    @FXML private TextField patronymicTextField;
    @FXML private DatePicker empDateField;
    @FXML private ComboBox<Speciality> specialityBox;
    @FXML private TextField loginTextField;
    @FXML private TextField passwordTextField;
    private final ObservableList<Speciality> specialities = FXCollections.observableArrayList(Speciality.values());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        specialityBox.setItems(specialities);
        specialityBox.getSelectionModel().selectFirst();

        Callback<ListView<Speciality>, ListCell<Speciality>> cellCallback = new Callback<>() {
            @Override
            public ListCell<Speciality> call(ListView<Speciality> doctorListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Speciality speciality, boolean empty) {
                        super.updateItem(speciality, empty);
                        if (speciality == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(speciality.name());
                        }
                    }
                };
            }
        };
        specialityBox.setButtonCell(cellCallback.call(null));
        specialityBox.setCellFactory(cellCallback);
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void save(ActionEvent event) {
        String surname = surnameTextField.getText();
        String name = nameTextField.getText();
        String patronymic = patronymicTextField.getText();
        Speciality speciality = specialityBox.getValue();
        LocalDate hiringDate = empDateField.getValue();
        String login = loginTextField.getText();
        String password = passwordTextField.getText();

        if(!check(surname, name, patronymic, hiringDate, login, password)){
            return;
        }
        Doctor doctor = new Doctor(surname, name, patronymic, speciality, hiringDate, login, password);
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(doctor);
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

    private boolean check(String surname, String name, String patronymic,
                          LocalDate hiringDate,
                          String login, String password){
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
            surnameTextField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            surnameTextField.setStyle("-fx-control-inner-background: white;");
        }

        if(!name.matches(NAME_PATTERN) || name.isEmpty()){
            nameTextField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            nameTextField.setStyle("-fx-control-inner-background: white;");
        }

        if(!patronymic.matches(PATRONYMIC_PATTERN) || patronymic.isEmpty()){
            patronymicTextField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else {
            patronymicTextField.setStyle("-fx-control-inner-background: white;");
        }

        if(hiringDate == null || hiringDate.isAfter(LocalDate.now())){
            empDateField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            empDateField.setStyle("-fx-control-inner-background: white;");
        }

        if(login.isEmpty() || admLog.contains(login) || recLog.contains(login)
                || docLog.contains(login) || patLog.contains(login)){
            loginTextField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            loginTextField.setStyle("-fx-control-inner-background: white;");
        }

        if(password.isEmpty()){
            passwordTextField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            passwordTextField.setStyle("-fx-control-inner-background: white;");
        }
        return correct;
    }
}
