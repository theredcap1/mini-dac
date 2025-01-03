package org.example.minidac;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ImageObj {
    public String name, path, classification;
    public long size;

    public ImageObj(String name, String path, long size, String classification) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.classification = classification;
    }
}

class ImageObjList {
    public ArrayList<ImageObj> images;

    public ImageObjList() {
        images = new ArrayList<>();
    }

    public String searchforPath(String name) {
        for (ImageObj image : images) {
            if (image.name.equals(name)) {
                return image.path;
            }
        }
        return null;
    }

    public long searchforSize(String name) {
        for (ImageObj image : images) {
            if (image.name.equals(name)) {
                return image.size;
            }
        }
        return 0;
    }
    public String searchforClassification(String name) {
        for (ImageObj image : images) {
            if (image.name.equals(name)) {
                return image.classification;
            }
        }
        return "";
    }
    public void setClassification(String name, String classification) {
        for (ImageObj image : images) {
            if (Objects.equals(image.name, name)) {
                image.classification = classification;
            }
        }
    }
    public void add(ImageObj image) {
        images.add(image);
    }
}

public class ClassificationController {

    @FXML
    private ListView<String> imageListView;
    @FXML
    private ImageView imageView;
    @FXML
    private VBox sidebar;
    @FXML
    private HBox myHBox;
    @FXML
    private Text name;
    @FXML
    private Text size;
    @FXML
    private Button exploreButton = new Button();
    @FXML
    private Text prob;

    private final ImageObjList imageObjList = new ImageObjList();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private boolean isSidebarVisible = false;

    public void initialize() {
        sidebar.setTranslateX(-250);
        myHBox.setPadding(new Insets(10));

        exploreButton.setOnAction(ignored -> toggleSidebar());
        imageListView.getSelectionModel().selectedItemProperty().addListener((ignored, ignored1, newValue) -> {
            if (newValue != null) {
                String imagePath = imageObjList.searchforPath(newValue);
                if (imagePath != null) {
                    Image image = new Image(imagePath);
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(400);

                    name.setText(newValue);
                    size.setText(imageObjList.searchforSize(newValue) / 1024 + "KB");
                    prob.setText(imageObjList.searchforClassification(newValue));
                }
            }
        });
    }

    public void toggleSidebar() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), sidebar);
        FadeTransition fade = new FadeTransition(Duration.millis(300), sidebar);
        if (isSidebarVisible) {
            slide.setToX(-250);
            fade.setToValue(0.0);
            fade.setOnFinished(ignored -> sidebar.setVisible(false));
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
        FileChooser.ExtensionFilter et = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg");
        fileChooser.getExtensionFilters().add(et);
        fileChooser.setTitle("Open Image Files");
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                imageListView.getItems().add(file.getName());
                imageObjList.add(new ImageObj(file.getName(), file.toURI().toString(), file.length(), ""));
            }
        }
    }

    public void classify() {
        String imageName = name.getText();
        if (imageName == null || imageName.isEmpty()) {
            prob.setText("Please select an image first!");
            return;
        }

        String path = imageObjList.searchforPath(imageName).substring(6);

        executorService.submit(() -> {
            try {
                File imageFile = new File(path);
                String classificationResult = ImageClassifier.classify(imageFile);
                imageObjList.setClassification(imageName, classificationResult);
                Platform.runLater(() -> prob.setText(classificationResult));

            } catch (Exception e) {
                Platform.runLater(() -> prob.setText("Error: " + e.getMessage()));
            }
        });
    }

    public void shutdownExecutor() {
        executorService.shutdown();
    }
}