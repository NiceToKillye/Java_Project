import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Loader extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage mainStage) throws Exception {
        Scene scene = new Scene(loadFXML("enterPanel"));
        mainStage.setTitle("Authorization");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Loader.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}