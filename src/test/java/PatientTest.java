import Entities.Patient;
import javafx.collections.FXCollections;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.time.LocalDate;
import java.util.List;

public class PatientTest extends TestCase {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session;

    Patient patient1;

    @Override
    protected void setUp() throws Exception {
        patient1 = new Patient("Testlogin", "Testpassword",
                "Testsurname", "TestName", "Testpatronymic",
                LocalDate.now(), "Testhome");
    }

    public void testName(){
        String name = "firstName";
        Patient patient = new Patient("login", "password",
                "surname", name, "patronymic",
                LocalDate.now(), "home");
        assertEquals(name, patient.getName());
    }

    public void testSurname(){
        String surname = "firstSurname";
        Patient patient = new Patient("login", "password",
                surname, "name", "patronymic",
                LocalDate.now(), "home");
        assertEquals(surname, patient.getSurname());
    }

    public void testSetLogin(){
        Patient patient = new Patient("login", "password",
                "surname","name", "patronymic",
                LocalDate.now(), "home");
        patient.setLogin("NewLogin");
        assertEquals("NewLogin", patient.getLogin());
    }

    public void testSetPassword(){
        Patient patient = new Patient("login", "password",
                "surname", "name", "patronymic",
                LocalDate.now(), "home");
        patient.setPassword("NewPassword");
        assertEquals("NewPassword", patient.getPassword());
    }

    public void testAdd(){
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(patient1);
            session.getTransaction().commit();
        }
        catch (Exception exception){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }

        List doctors = FXCollections.observableArrayList(
                session.createQuery("from Patient", Patient.class).getResultList());
        assertTrue(doctors.contains(patient1));
        session.close();
    }

    @Override
    protected void tearDown() throws Exception {
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(patient1);
            session.getTransaction().commit();
        }
        catch (Exception ex){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();
    }
}
