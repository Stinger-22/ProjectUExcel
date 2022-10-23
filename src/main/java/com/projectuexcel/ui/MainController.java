package com.projectuexcel.ui;

import com.projectuexcel.mail.CodeMail;
import com.projectuexcel.mail.MailSender;
import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;
import com.projectuexcel.table.export.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class MainController {
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
    private Button choosePlan;
    @FXML
    private Pane paneSend;
    @FXML
    private Pane paneExport;
    @FXML
    private Pane paneViewEmails;

    private MailSender mailSender;
    private Map<String, String> mailsMap;
    private ObservableList<CodeMail> tableData;
    private Plan plan;

    @FXML
    public void initialize() throws FileNotFoundException {
        this.mailSender = MailSender.getMailSender();
        this.mailsMap = importEmails("test_codemail.txt");

        this.codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        this.emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loadCodeMailTableView();
        setupFilter();
    }

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

    public void exportAll(ActionEvent actionEvent) throws IOException, InvalidFormatException {
        openPlan();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String path = directoryChooser.showDialog(null).getAbsolutePath() + "\\";
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        Exporter exporter = getChosenExporter();
        for (Teacher teacher : teacherTablePlacement) {
            //TODO: make export .xls or .xlsx depending on origin file
            exporter.export(teacher, path + teacher.getCode() + ".xlsx");
        }
    }

    public void exportOne(ActionEvent actionEvent) throws IOException, InvalidFormatException {
        openPlan();

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

    private void openPlan() throws IOException, InvalidFormatException {
        String sourcePath = pathPlan.getText();
        if (plan != null && plan.getFile().getAbsolutePath().equals(sourcePath)) {
            return;
        }
        try {
            this.plan = new Plan(sourcePath);
        }
        catch (OldExcelFormatException exception) {
            //TODO MessageBox Error
            System.out.println("Convert file to .xlsx");
            exception.printStackTrace();
        }
    }

    private Map<String, String> importEmails(String path) throws FileNotFoundException {
        Map<String, String> emails = new HashMap<>();
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        String[] line;
        while (scanner.hasNext()) {
            line = scanner.nextLine().split("=");
            emails.put(line[0].trim(), line[1].trim());
        }
        return emails;
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

    //TODO create new thread for sending messages
    public void sendOne(ActionEvent actionEvent) throws IOException, InvalidFormatException {
        openPlan();

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
        controller.setup(plan, getChosenExporter(), mailsMap, subject.getText(), text.getHtmlText());
        stage.show();
    }

    public void sendAll(ActionEvent actionEvent) throws MessagingException, IOException, InvalidFormatException {
        //TODO progressbar
        openPlan();
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        String exportPath = "Plan.xlsx";
        File file;
        Exporter exporter = getChosenExporter();
        for (Teacher teacher : teacherTablePlacement) {
            file = new File(exportPath);
            exporter.export(teacher, exportPath);
            mailSender.setAttachment(file);
            //TODO handle when no mail found
            String mail = mailsMap.get(teacher.getCode());
            if (mail == null) {
                continue;
            }
            mailSender.sendMessageAttachment(mail, subject.getText(), text.getHtmlText());
            file.delete();
        }
    }

    public void sendOriginToAll(ActionEvent actionEvent) throws MessagingException, IOException, InvalidFormatException {
        openPlan();
        List<Teacher> teachers = plan.getTeacherTablePlacement();
        mailSender.setAttachment(plan.getFile());
        String mail;

        for (Teacher teacher : teachers) {
            //TODO handle when no code
            mail = mailsMap.get(teacher.getCode());
            mailSender.sendMessageAttachment(mail, subject.getText(), text.getHtmlText());
        }
    }

    private void loadCodeMailTableView() {
        tableData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mailsMap.keySet());
        for (String key : keys) {
            tableData.add(new CodeMail(key, mailsMap.get(key)));
        }
    }

    public void changeEmails() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            mailsMap = importEmails(file.getAbsolutePath());
            loadCodeMailTableView();
            setupFilter();
        }
    }

    private void setupFilter() {
        FilteredList<CodeMail> filteredList = new FilteredList<CodeMail>(tableData, p -> true);
        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(codeMail -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                if (codeMail.getCode().contains(newValue)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<CodeMail> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(codeMailTableView.comparatorProperty());
        codeMailTableView.setItems(sortedList);
    }
}
