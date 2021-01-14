import Entities.Doctor;
import Entities.Speciality;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.time.LocalDate;
import java.util.List;


public class DoctorTests extends TestCase {

    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure("hibernate.cfg.xml").build();
    Metadata metadata = new MetadataSources(registry)
            .getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata
            .getSessionFactoryBuilder().build();
    Session session;

    Doctor doctor1;

    @Override
    protected void setUp() throws Exception {
        doctor1 = new Doctor("TestSurname", "TestName", "TestPatronymic",
                Speciality.DENTIST, LocalDate.now(), "TestLogin", "TestPassword");
    }

    public void testName(){
        String name = "firstName";
        Doctor doctor = new Doctor("surname", name, "patronymic",
                Speciality.DENTIST, LocalDate.now(),
                "login", "password");
        assertEquals(name, doctor.getName());
    }

    public void testSurname(){
        String surname = "firstSurname";
        Doctor doctor = new Doctor(surname, "name", "patronymic",
                Speciality.DENTIST, LocalDate.now(),
                "login", "password");
        assertEquals(surname, doctor.getSurname());
    }

    public void testSetLogin(){
        Doctor doctor = new Doctor("surname", "name", "patronymic",
                Speciality.DENTIST, LocalDate.now(),
                "login", "password");
        doctor.setLogin("NewLogin");
        assertEquals("NewLogin", doctor.getLogin());
    }

    public void testSetPassword(){
        Doctor doctor = new Doctor("surname", "name", "patronymic",
                Speciality.DENTIST, LocalDate.now(),
                "login", "password");
        doctor.setPassword("NewPassword");
        assertEquals("NewPassword", doctor.getPassword());
    }

    public void testAdd(){
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(doctor1);
            session.getTransaction().commit();
        }
        catch (Exception exception){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }

        List doctors = session.createQuery("from Doctor", Doctor.class).getResultList();
        assertTrue(doctors.contains(doctor1));

        session.close();
    }

    @Override
    protected void tearDown() throws Exception {
        session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(doctor1);
            session.getTransaction().commit();
        }
        catch (Exception ex){
            session.getTransaction().rollback();
            System.out.println("The transaction was not completed");
        }
        session.close();
    }
}
