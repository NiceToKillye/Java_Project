package Controllers;

import Entities.Patient;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PatientPanel implements Initializable {

    private final static String SURNAME_PATTERN = "[А-ЯЁ][а-яё]++(?:-[А-ЯЁ][а-яё]++)?";
    private final static String NAME_PATTERN = "[А-ЯЁ][а-яё]++";
    private final static String PATRONYMIC_PATTERN = "[А-ЯЁ][а-яё]++";

    Patient patient;
    boolean receptionist;
    boolean admin;

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session = sessionFactory.openSession();

    ObservableList<PatientCard> cards;

    public PatientPanel(Patient patient, boolean receptionist, boolean admin){
        this.patient = patient;
        this.receptionist = receptionist;
        this.admin = admin;
    }

    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button changeUserButton;
    @FXML private TextField surnameTextField;
    @FXML private TextField nameTextField;
    @FXML private TextField patronymicTextField;
    @FXML private DatePicker birthdayDateField;
    @FXML private TextField homeTextField;
    @FXML private TextField idTextField;
    @FXML private TableView<PatientCard> cardsTable;
    @FXML private TableColumn<PatientCard, String> idColumn;
    @FXML private TableColumn<PatientCard, LocalDate> initialDateColumn;
    @FXML private TableColumn<PatientCard, String> diagnosisColumn;
    @FXML private TableColumn<PatientCard, LocalDate> invoiceDateColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(receptionist || admin){
            changeUserButton.setDisable(true);
            changeUserButton.setVisible(false);
        }

        try {
            cards = FXCollections.observableArrayList(
                    session.createQuery("from PatientCard where patient_id=:patient_id")
                            .setParameter("patient_id", patient.getId()).getResultList());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        surnameTextField.setText(patient.getSurname());
        nameTextField.setText(patient.getName());
        patronymicTextField.setText(patient.getPatronymic());
        birthdayDateField.setValue(patient.getBirthdayDate());
        homeTextField.setText(patient.getHomeAddress());
        idTextField.setText(Integer.toString(patient.getId()));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("docFIO"));
        initialDateColumn.setCellValueFactory(new PropertyValueFactory<>("initialDate"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        invoiceDateColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceDate"));

        cardsTable.setItems(cards);

        cardsTable.setRowFactory(tv -> {
            TableRow<PatientCard> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    PatientCard rowData = row.getItem();
                    Card cardPanel;
                    if(admin){
                        cardPanel = new Card(rowData, true);
                    }
                    else {
                        cardPanel = new Card(rowData, false);
                    }
                    try {
                        createWindow(cardPanel);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Double click on: " + rowData.getPatientId() + " pat ID");
                }
            });
            return row;
        });
    }

    @FXML
    void cancel(ActionEvent event) {
        if(receptionist || admin){
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
        else{
            surnameTextField.setText(patient.getSurname());
            nameTextField.setText(patient.getName());
            patronymicTextField.setText(patient.getPatronymic());
            birthdayDateField.setValue(patient.getBirthdayDate());
            homeTextField.setText(patient.getHomeAddress());
            idTextField.setText(Integer.toString(patient.getId()));
        }
    }

    @FXML
    void changeUser(ActionEvent event) throws IOException {
        session.close();
        changeScene(event, "/enterPanel", "Enter Panel");
    }

    @FXML
    void save(ActionEvent event) {
        String surname = surnameTextField.getText();
        String name = nameTextField.getText();
        String patronymic = patronymicTextField.getText();
        LocalDate birthday = birthdayDateField.getValue();
        String homeAddress = homeTextField.getText();

        if(!checkCorrect(surname, name, patronymic,
                birthday, homeAddress)){
            return;
        }

        patient.setSurname(surname);
        patient.setName(name);
        patient.setPatronymic(patronymic);
        patient.setBirthdayDate(birthday);
        patient.setHomeAddress(homeAddress);

        try {
            session.beginTransaction();
            session.saveOrUpdate(patient);
            session.getTransaction().commit();
        }
        catch (Exception exception){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }

        if(receptionist || admin){
            session.close();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
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

    private void createWindow(Card card) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/card" + ".fxml"));
        fxmlLoader.setController(card);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("New Card");
        stage.setScene(scene);
        stage.show();
    }

    private boolean checkCorrect(String surname, String name, String patronymic,
                                 LocalDate birthday, String home){

        boolean correct = true;

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

        if(birthday == null || birthday.isAfter(LocalDate.now())){
            birthdayDateField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            birthdayDateField.setStyle("-fx-control-inner-background: white;");
        }

        if(home.isEmpty()){
            homeTextField.setStyle("-fx-control-inner-background: red;");
            correct = false;
        }
        else{
            homeTextField.setStyle("-fx-control-inner-background: white;");
        }

        return correct;
    }
}
