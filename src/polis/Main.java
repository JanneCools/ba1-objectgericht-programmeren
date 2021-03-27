package polis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Hier aanvullen, je wil wellicht niet gewoon maar een HBox tonen?
        Parent root = FXMLLoader.load(getClass().getResource("polis.fxml"));
        Scene scene = new Scene (root);
        stage.setScene(scene);
        stage.setTitle("Polis - 2021 © Universiteit Gent");
        stage.show();
    }

}
