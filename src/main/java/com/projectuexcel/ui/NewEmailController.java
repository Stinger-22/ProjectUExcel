package com.projectuexcel.ui;

import com.projectuexcel.concurrency.Monitor;
import com.projectuexcel.concurrency.WaitForString;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class NewEmailController {
    @FXML
    private Text textNotFound;
    @FXML
    private TextField email;
    @FXML
    private Button buttonSkip;
    @FXML
    private Button buttonAddEmail;

    private boolean add = false;
    private WaitForString waitForString;

    public void setup(WaitForString string, String code) {
        this.waitForString = string;
        this.textNotFound.setText(textNotFound.getText() + code);
    }

    public void addEmail(ActionEvent actionEvent) {
        add = true;
        waitForString.setString(email.getText());
        waitForString.doNotify();
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void skip(ActionEvent actionEvent) {
        waitForString.doNotify();
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public boolean isAdd() {
        return add;
    }

    public String getEmail() {
        return email.getText();
    }
}
