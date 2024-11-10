package org.example.minidac;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class HelloController {

    @FXML
    private ListView<String> imageListView;
    @FXML
    private ImageView imageView;
    @FXML
    private VBox sidebar;
    @FXML
    private HBox myHBox;

    @FXML
    private Button exploreButton = new Button();  // Button to trigger the sidebar

    private boolean isSidebarVisible = false;
    public void initialize() {
        // Initialize sidebar off-screen to the left
        sidebar.setTranslateX(-250); // Adjust based on sidebar width
        myHBox.setPadding(new Insets(10)); // Padding for the HBox

        // Set up the Explore button action
        exploreButton.setOnAction(event -> toggleSidebar());
    }

    // Toggle the sidebar visibility with animation
    public void toggleSidebar() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), sidebar);
        FadeTransition fade = new FadeTransition(Duration.millis(300), sidebar);
        if (isSidebarVisible) {
            slide.setToX(-250);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> sidebar.setVisible(false));
        } else {
            sidebar.setVisible(true);
            slide.setToX(0);
            fade.setToValue(1.0);
        }

        slide.play();
        fade.play();
        isSidebarVisible = !isSidebarVisible;
    }


    public void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        File selectedFile = fileChooser.showOpenDialog(null);
        System.out.println((long)(selectedFile.length()/1024) + "KB");

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
            // Add the filename to the imageListView
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(400);
            imageListView.getItems().add(selectedFile.getName());
        }
    }
}