package com.projectuexcel.ui;

import com.projectuexcel.concurrency.WaitForString;
import com.projectuexcel.mail.MailSender;
import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;
import com.projectuexcel.table.export.*;
import com.projectuexcel.ui.table.CodeMail;
import com.projectuexcel.ui.table.DateName;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class MainController {
    @FXML
    private TextField addCode;
    @FXML
    private TextField addEmail;
    @FXML
    private TextField filter;
    @FXML
    private TableColumn<Map, String> codeColumn;
    @FXML
    private TableColumn<Map, String> emailColumn;
    @FXML
    private TableView<CodeMail> codeMailTableView;
    @FXML
    private HTMLEditor text;
    @FXML
    private TextField subject;
    @FXML
    private ToggleGroup partition;
    @FXML
    private RadioButton selectYear;
    @FXML
    private RadioButton selectFirstSemester;
    @FXML
    private RadioButton selectSecondSemester;
    @FXML
    private TextField pathPlan;
    @FXML
    private TableView<DateName> planHistory;
    @FXML
    private TableColumn<Map, String> dateColumn;
    @FXML
    private TableColumn<Map, String> fileNameColumn;

    private MailSender mailSender;
    private Map<String, List<String>> mailsMap;
    private ObservableList<CodeMail> tableData;
    private ObservableList<DateName> historyData;
    private Plan plan;
    private File emailsFile;

    @FXML
    public void initialize() throws FileNotFoundException, AddressException {
        this.mailSender = MailSender.getMailSender();
        setupEmailTable();
        setupHistoryTable();
    }

    private void setupEmailTable() {
        this.codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        this.emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        this.codeMailTableView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                CodeMail selected = codeMailTableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                        tableData.remove(selected);
                        setupFilter();
                    }
                }
            }
        });
    }

    private void setupHistoryTable() throws FileNotFoundException {
        planHistory.setRowFactory( tableView -> {
            TableRow<DateName> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    DateName dateName = row.getItem();
                    try {
                        Desktop.getDesktop().open(new File("history\\" + dateName.getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });
        this.dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        File historyTxt = new File("history\\history.txt");
        Scanner scanner = new Scanner(historyTxt);
        String[] line;
        historyData = FXCollections.observableArrayList();
        while (scanner.hasNext()) {
            line = scanner.nextLine().split("=");
            String date = line[0].trim();
            String name = line[1].trim();
            historyData.add(new DateName(date, name));
        }
        planHistory.setItems(historyData);
    }


    public void selectTable(ActionEvent actionEvent) throws IOException, InvalidFormatException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pathPlan.setText(file.getAbsolutePath());
        }
        try {
            this.plan = new Plan(pathPlan.getText());
        }
        catch (OldExcelFormatException exception) {
            pathPlan.setText(null);
            Alert alert = new Alert(Alert.AlertType.ERROR, "File was created using Excel 5 which is too old. Save file using Excel in new format and try again.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void exportAll(ActionEvent actionEvent) {
        if (plan == null) {
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        String path = file.getAbsolutePath() + "\\";
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        Exporter exporter = getChosenExporter();
        for (Teacher teacher : teacherTablePlacement) {
            exporter.export(teacher, path + teacher.getCode() + ".xlsx");
        }
    }

    public void exportOne(ActionEvent actionEvent) throws IOException {
        if (plan == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/exportOne.fxml"));

        VBox vbox = loader.load();
        Scene scene = new Scene(vbox);

        Stage stage = new Stage();
        stage.setTitle("Export one");
        stage.getIcons().add(new Image("file:planExporter.png"));
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        ExportOneController controller = loader.getController();
        controller.setup(plan, getChosenExporter());
        stage.show();
    }

    private Map<String, List<String>> importEmails(String path) throws FileNotFoundException {
        Map<String, List<String>> importedEmails = new HashMap<>();
        emailsFile = new File(path);
        Scanner scanner = new Scanner(emailsFile);
        String[] line;
        while (scanner.hasNext()) {
            line = scanner.nextLine().split("=");
            List<String> tempMails = new ArrayList<>();
            String code = line[0].trim();
            line = line[1].split(",");
            for (String mail : line) {
                tempMails.add(mail.trim());
            }
            importedEmails.put(code, tempMails);
        }
        return importedEmails;
    }

    private TeacherExporter getChosenExporter() {
        Toggle selectedToggle = partition.getSelectedToggle();
        if (selectYear.equals(selectedToggle)) {
            return new TeacherExporterYear(plan);
        } else if (selectFirstSemester.equals(selectedToggle)) {
            return new TeacherExporterFirstSemester(plan);
        } else if (selectSecondSemester.equals(selectedToggle)) {
            return new TeacherExporterSecondSemester(plan);
        }
        throw new IllegalStateException();
    }

    public void sendOne(ActionEvent actionEvent) throws IOException {
        if (plan == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sendOne.fxml"));

        VBox vbox = loader.load();
        Scene scene = new Scene(vbox);

        Stage stage = new Stage();
        stage.setTitle("Send one");
        stage.getIcons().add(new Image("file:planExporter.png"));
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        SendOneController controller = loader.getController();
        controller.setup(plan, getChosenExporter(), getCurrentEmailTableData(), subject.getText(), text.getHtmlText());
        stage.show();
    }

    public void sendAll(ActionEvent actionEvent) {
        if (plan == null) {
            return;
        }
        WaitForString waitForString = new WaitForString();
        SendAllController controller;
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        try {
            controller = setupSendingWindow();
            controller.setup(1.0 / teacherTablePlacement.size());
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't show sending window. Messages won't be send.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String exportPath = "Plan.xlsx";
                File file;
                Exporter exporter = getChosenExporter();
                for (Teacher teacher : teacherTablePlacement) {
                    file = new File(exportPath);
                    exporter.export(teacher, exportPath);
                    mailSender.setAttachment(file);
                    List<String> mailList = getCurrentEmailTableData().get(teacher.getCode());
                    if (mailList == null) {
                        handleNoEmail(waitForString, controller, teacher);
                    }
                    else {
                        String[] mailArray = new String[mailList.size()];
                        try {
                            mailSender.sendMessageAttachment(mailList.toArray(mailArray), subject.getText(), text.getHtmlText());
                            controller.messageSent(teacher.getCode(), mailArray);
                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                        }
                        file.delete();
                    }
                }
                controller.finish();
            }
        });
        thread.start();
    }

    private SendAllController setupSendingWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sending.fxml"));

        VBox vbox = loader.load();
        Scene scene = new Scene(vbox);

        Stage stage = new Stage();
        stage.setTitle("Sending process");
        stage.getIcons().add(new Image("file:planExporter.png"));
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        SendAllController controller = loader.getController();
        stage.show();
        return controller;
    }

    public void sendOriginToAll(ActionEvent actionEvent) {
        if (plan == null) {
            return;
        }
        WaitForString waitForString = new WaitForString();
        SendAllController controller;
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        try {
            controller = setupSendingWindow();
            controller.setup(1.0 / teacherTablePlacement.size());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can't show sending window. Messages won't be send.\nError:\n" + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mailSender.setAttachment(plan.getFile());
                for (Teacher teacher : teacherTablePlacement) {
                    List<String> mailList = getCurrentEmailTableData().get(teacher.getCode());
                    if (mailList == null) {
                        handleNoEmail(waitForString, controller, teacher);
                    }
                    else {
                        String[] mailArray = new String[mailList.size()];
                        try {
                            mailSender.sendMessageAttachment(mailList.toArray(mailArray), subject.getText(), text.getHtmlText());
                            controller.messageSent(teacher.getCode(), mailArray);
                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                controller.finish();
            }
        });
        thread.start();
    }

    private void handleNoEmail(WaitForString waitForString, SendAllController controller, Teacher teacher) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    emailNotFound(waitForString, teacher.getCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        waitForString.doWait();
        if (waitForString.getString() != null) {
            try {
                addCodeMail(teacher.getCode(), waitForString.getString());
                mailSender.sendMessageAttachment(waitForString.getString(), subject.getText(), text.getHtmlText());
                controller.messageSent(teacher.getCode(), waitForString.getString());
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
            }
        }
        else {
            controller.messageNotSent(teacher.getCode());
        }
    }

    private void loadCodeMailTableView() {
        tableData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mailsMap.keySet());
        for (String key : keys) {
            List<String> emails = mailsMap.get(key);
            for (String email : emails) {
                tableData.add(new CodeMail(key, email));
            }
        }
    }

    public void changeEmails() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            saveEmailTableChanges();
            emailsFile = file;
            mailsMap = importEmails(emailsFile.getAbsolutePath());
            loadCodeMailTableView();
            setupFilter();
        }
    }

    public void setupFilter() {
        FilteredList<CodeMail> filteredList = new FilteredList<>(tableData, p -> true);
        filter.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(codeMail -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            return codeMail.getCode().contains(newValue);
        }));

        SortedList<CodeMail> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(codeMailTableView.comparatorProperty());
        codeMailTableView.setItems(sortedList);
    }

    public void addCodeMail(ActionEvent actionEvent) {
        tableData.add(new CodeMail(addCode.getText(), addEmail.getText()));
        setupFilter();
    }

    public Map<String, List<String>> getCurrentEmailTableData() {
        Map<String, List<String>> data = new HashMap<>();
        for (CodeMail codeMail : tableData) {
            if (data.containsKey(codeMail.getCode())) {
                data.get(codeMail.getCode()).add(codeMail.getEmail());
            }
            else {
                List<String> emails = new ArrayList<>();
                emails.add(codeMail.getEmail());
                data.put(codeMail.getCode(), emails);
            }
        }
        return data;
    }

    public void saveEmailTableChanges() throws IOException {
        if (emailsFile == null) {
            return;
        }
        FileWriter fileWriter = new FileWriter(emailsFile, false);
        Map<String, List<String>> emailData = getCurrentEmailTableData();
        List<String> keys = new ArrayList<>(emailData.keySet());
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : keys) {
            stringBuilder.setLength(0);
            stringBuilder.append(key).append("=");
            List<String> emails = emailData.get(key);
            for (int i = 0; i < emails.size() - 1; i++) {
                stringBuilder.append(emails.get(i)).append(",");
            }
            stringBuilder.append(emails.get(emails.size() - 1)).append("\n");
            fileWriter.write(stringBuilder.toString());
        }
        fileWriter.close();
    }

    private void emailNotFound(WaitForString waitForString, String code) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/newEmail.fxml"));

        VBox vbox = loader.load();
        Scene scene = new Scene(vbox);

        Stage stage = new Stage();
        stage.setTitle("New email");
        stage.getIcons().add(new Image("file:planExporter.png"));
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        NewEmailController controller = loader.getController();
        controller.setup(waitForString, code);
        stage.show();
    }

    private void addCodeMail(String code, String mail) {
        tableData.add(new CodeMail(code, mail));
        setupFilter();
    }

    public Plan getPlan() {
        return plan;
    }
}
