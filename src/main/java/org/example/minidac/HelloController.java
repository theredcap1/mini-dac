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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

class ImageObj {
    public String name, path;
    public long size;

    public ImageObj(String name, String path, long size) {
        this.name = name;
        this.path = path;
        this.size = size;
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
    public void add(ImageObj image) {
        images.add(image);
    }
}
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
    private Text name;
    @FXML
    private Text size;
    @FXML
    private Button exploreButton = new Button();  // Button to trigger the sidebar
    private final ImageObjList imageObjList = new ImageObjList();
    private boolean isSidebarVisible = false;
    public void initialize() {
        sidebar.setTranslateX(-250);
        myHBox.setPadding(new Insets(10));

        exploreButton.setOnAction(ignored -> toggleSidebar());
        imageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String imagePath = imageObjList.searchforPath(newValue);
                if (imagePath != null) {
                    Image image = new Image(imagePath);
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(400);

                    name.setText(newValue);
                    File file = new File(imagePath);
                    System.out.println(imagePath);
                    size.setText(imageObjList.searchforSize(newValue) / 1024 + "KB");
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
        fileChooser.setTitle("Open Image File");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(400);

            imageListView.getItems().add(selectedFile.getName());
            imageObjList.add(new ImageObj(selectedFile.getName(), selectedFile.toURI().toString(), selectedFile.length()));
            name.setText(selectedFile.getName());
            size.setText(selectedFile.length() / 1024 + "KB");
        }
    }
}