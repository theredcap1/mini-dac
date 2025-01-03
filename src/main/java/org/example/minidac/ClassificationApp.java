package org.example.minidac;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClassificationApp extends Application {

    private ClassificationController helloController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainLayout.fxml"));
        Parent root = loader.load();

        helloController = loader.getController();

        primaryStage.setTitle("Image Viewer");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (helloController != null) {
            helloController.shutdownExecutor();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}