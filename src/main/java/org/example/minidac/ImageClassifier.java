package org.example.minidac;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;

import java.io.File;

public class ImageClassifier {
    public static String classify(File imageFile) {
        try {
            // Convert the file to a DJL Image
            Image img = ImageFactory.getInstance().fromFile(imageFile.toPath());
            System.out.println(imageFile.toPath());
            // Define the model criteria
            Criteria<Image, Classifications> criteria =
                    Criteria.builder()
                            .optApplication(Application.CV.IMAGE_CLASSIFICATION)
                            .setTypes(Image.class, Classifications.class)
                            .build();

            // Load the model
            try (ZooModel<Image, Classifications> model = criteria.loadModel();
                 Predictor<Image, Classifications> predictor = model.newPredictor()) {


                Classifications classifications = predictor.predict(img);
                System.out.println(classifications.best());
                double prob = classifications.best().getProbability();
                String name = classifications.best().getClassName().replaceAll("n\\d+", "");
                return "This image is " + prob * 100 + "%" + " a" + name;
            }
        } catch (Exception e) {
            return "Error during classification: " + e.getMessage();
        }
    }
}
