import Entities.Admin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.util.List;

public class Loader extends Application {
    public static void main(String[] args) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry)
                .getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata
                .getSessionFactoryBuilder().build();
        Session session = sessionFactory.openSession();

        List<Admin> adminList = session.createQuery("from Admin", Admin.class).getResultList();
        adminList.forEach(admin -> System.out.println(admin.getLogin()));

        launch();
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Scene scene = new Scene(loadFXML("card"));
        mainStage.setTitle("Authorization");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Loader.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
