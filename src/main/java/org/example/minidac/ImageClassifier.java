package org.example.minidac;

import ai.djl.Application;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;

import java.io.File;
import java.text.DecimalFormat;

public class ImageClassifier {

    public static String classify(File imageFile) {
        try {
            Image img = ImageFactory.getInstance().fromFile(imageFile.toPath());

            Criteria<Image, Classifications> criteria =
                    Criteria.builder()
                            .optApplication(Application.CV.IMAGE_CLASSIFICATION)
                            .setTypes(Image.class, Classifications.class)
                            .build();

            try (ZooModel<Image, Classifications> model = criteria.loadModel();
                 Predictor<Image, Classifications> predictor = model.newPredictor()) {

                Classifications classifications = predictor.predict(img);

                double prob = classifications.best().getProbability() * 100;
                DecimalFormat df = new DecimalFormat("#.####");
                String name = classifications.best().getClassName().replaceAll("n\\d+", "");

                return "This image is " + df.format(prob) + "% a " + name;
            }
        } catch (Exception e) {
            return "Error during classification: " + e.getMessage();
        }
    }

    public static void classifyAsync(File imageFile, ClassificationCallback callback) {
        new Thread(() -> {
            try {
                String result = classify(imageFile);
                callback.onSuccess(result);
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public interface ClassificationCallback {
        void onSuccess(String result);

        void onError(Exception e);
    }
}