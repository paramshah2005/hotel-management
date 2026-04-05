package com.hotel.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getStage() {
        return primaryStage;
    }

    public static void switchScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}