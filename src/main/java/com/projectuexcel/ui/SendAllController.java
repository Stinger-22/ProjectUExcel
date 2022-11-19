package com.projectuexcel.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class SendAllController {
    @FXML
    private ProgressBar progressBar;
    private double d;
    private double progress;

    @FXML
    public VBox labelBox;
    @FXML
    private Button buttonOk;

    public void setup(double d) {
        this.d = d;
        this.progress = 0.0;
    }

    public void ok(ActionEvent actionEvent) {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void finish() {
        buttonOk.setDisable(false);
    }

    public void messageSent(String code, String email) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label label = new Label();
                label.setText("Message was sent to " + code + " with email " + email);
                label.setWrapText(true);
                labelBox.getChildren().add(label);
                updateProgress();
            }
        });
    }

    public void messageSent(String code, String[] emails) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label label = new Label();
                label.setText("Message was sent to " + code + " with email " + Arrays.toString(emails));
                label.setWrapText(true);
                labelBox.getChildren().add(label);
                updateProgress();
            }
        });
    }

    public void messageNotSent(String code) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label label = new Label();
                label.setText("Message was not sent to " + code);
                label.setWrapText(true);
                label.setTextFill(Color.color(1, 0, 0));
                labelBox.getChildren().add(label);
                updateProgress();
            }
        });
    }

    private void updateProgress() {
        progress += d;
        progressBar.setProgress(progress);
    }
}
