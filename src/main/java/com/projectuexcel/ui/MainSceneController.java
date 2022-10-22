package com.projectuexcel.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;

public class MainSceneController {
    @FXML
    private TextField pathPlan;
    @FXML
    private Button choosePlan;
    @FXML
    private Pane paneSend;
    @FXML
    private Pane paneExport;
    @FXML
    private Pane paneViewEmails;

    public void selectTable(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pathPlan.setText(file.getAbsolutePath());
        }
    }
}
