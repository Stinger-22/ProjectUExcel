package com.projectuexcel.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationWindow extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        setupMainStage(primaryStage);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main.fxml"));

        VBox vbox = loader.load();
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void setupMainStage(Stage stage) {
        stage.setTitle("Plan Exporter");
        stage.getIcons().add(new Image("file:planExporter.png"));
    }
}
