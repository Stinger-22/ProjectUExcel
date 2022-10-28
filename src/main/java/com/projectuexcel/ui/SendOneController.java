package com.projectuexcel.ui;

import com.projectuexcel.mail.MailSender;
import com.projectuexcel.table.Plan;
import com.projectuexcel.table.Teacher;
import com.projectuexcel.table.export.Exporter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.projectuexcel.table.Teacher.searchTeacherByCode;

public class SendOneController {
    @FXML
    private Button buttonOk;
    @FXML
    private Button buttonCancel;
    @FXML
    private TextField code;

    private Plan plan;
    private Exporter exporter;
    private Map<String, List<String>> mails;
    private String subject;
    private String text;

    public void setup(Plan plan, Exporter exporter, Map<String, List<String>> mails, String subject, String text) {
        this.plan = plan;
        this.exporter = exporter;
        this.mails = mails;
        this.subject = subject;
        this.text = text;
    }

    public void cancel(ActionEvent actionEvent) {
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

    public void ok(ActionEvent actionEvent) throws MessagingException, IOException {
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();

        int i = searchTeacherByCode(teacherTablePlacement, code.getText());
        if (i == -1) {
//            TODO MessageBox Error
            System.out.println("Викладача з такими кодом не існує");
            return;
        }
        Teacher teacher = teacherTablePlacement.get(i);
        List<String> mailList = mails.get(teacher.getCode());
        if (mailList == null) {
            //TODO Handle error
            return;
        }

        String exportPath = "Plan.xlsx";
        File file = new File(exportPath);
        exporter.export(teacher, exportPath);
        MailSender mailSender = MailSender.getMailSender();
        mailSender.setAttachment(file);
        String[] mailArray = new String[mailList.size()];
        mailSender.sendMessageAttachment(mailList.toArray(mailArray), subject, text);
        file.delete();

        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }
}
