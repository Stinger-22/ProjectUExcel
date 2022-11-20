package com.projectuexcel.ui;

import com.projectuexcel.table.Plan;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class ApplicationWindow extends Application {
    private MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupMainStage(primaryStage);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main.fxml"));

        VBox vbox = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws IOException {
        controller.saveEmailTableChanges();
        Plan plan = controller.getPlan();
        if (plan != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Save file in history?.", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> answer = alert.showAndWait();
            if (answer.isPresent()  && answer.get() == ButtonType.YES) {
                FileWriter fileWriter = new FileWriter("history\\history.txt", true);
                fileWriter.write(plan.getDate() + "=" + plan.getFile().getName());
                fileWriter.close();
                Files.copy(plan.getFile().toPath(), (new File("history/" + plan.getFile().getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }

    private void setupMainStage(Stage stage) {
        stage.setTitle("Plan Exporter");
        stage.getIcons().add(new Image("file:planExporter.png"));
    }
}
