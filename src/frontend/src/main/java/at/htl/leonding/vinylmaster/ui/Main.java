package at.htl.leonding.vinylmaster.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Vinyl Master - Collection Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Close database connection pool
        at.htl.leonding.vinylmaster.config.DatabaseConfig.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
