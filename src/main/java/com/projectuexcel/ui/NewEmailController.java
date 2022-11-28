package com.projectuexcel.ui;

import com.projectuexcel.concurrency.WaitForString;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class NewEmailController {
    @FXML
    private Text textNotFound;
    @FXML
    private TextField email;

    private WaitForString waitForString;

    public void setup(WaitForString string, String code) {
        this.waitForString = string;
        this.textNotFound.setText(textNotFound.getText() + code);
    }

    public void addEmail(ActionEvent actionEvent) {
        waitForString.setString(email.getText());
        waitForString.doNotify();
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void skip(ActionEvent actionEvent) {
        waitForString.doNotify();
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
}
