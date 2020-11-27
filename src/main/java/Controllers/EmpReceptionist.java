package Controllers;

import Entities.Receptionist;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmpReceptionist implements Initializable {
    private final static String SURNAME_PATTERN = "[А-ЯЁ][а-яё]++(?:-[А-ЯЁ][а-яё]++)?";
    private final static String NAME_PATTERN = "[А-ЯЁ][а-яё]++";

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
    @FXML private TextField loginTextField;
    @FXML private TextField passwordTextField;

    public EmpReceptionist(){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        if(!check(surname, name, login, password)){
            return;
        }
        session = sessionFactory.openSession();
        Receptionist receptionist = new Receptionist(surname, name, login, password);
        try {
            session.beginTransaction();
            session.save(receptionist);
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

    boolean check(String surname, String name,
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
