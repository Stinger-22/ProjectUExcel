package com.projectuexcel.ui;

import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;
import com.projectuexcel.table.export.Exporter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.util.List;

import static com.projectuexcel.table.Teacher.searchTeacherByCode;

public class ExportOneController {
    @FXML
    private TextField pathFolder;
    @FXML
    private TextField code;

    private Plan plan;
    private Exporter exporter;

    public void setup(Plan plan, Exporter exporter) {
        this.plan = plan;
        this.exporter = exporter;
    }

    public void selectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String path = directoryChooser.showDialog(null).getAbsolutePath() + "\\";
        pathFolder.setText(path);
    }

    public void cancel(ActionEvent actionEvent) {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void ok(ActionEvent actionEvent) {
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        int i = searchTeacherByCode(teacherTablePlacement, code.getText());
        if (i == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No teacher with existing code.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        exporter.export(teacherTablePlacement.get(i), pathFolder.getText() + code.getText() + ".xlsx");
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
}
