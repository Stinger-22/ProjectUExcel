package com.projectuexcel;

import com.projectuexcel.mail.MailSender;
import com.projectuexcel.table.Teacher;
import com.projectuexcel.util.ConsoleInput;
import com.projectuexcel.table.Plan;
import com.projectuexcel.table.export.*;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ApplicationConsole {
    private Scanner input = ConsoleInput.getScanner();

    private final MailSender mailSender;
    private Plan plan;
    private Map<String, String> mails;

    public ApplicationConsole() throws FileNotFoundException {
        this.mailSender = new MailSender();
        this.mails = importEmails("test_codemail.txt");
    }

    public void run() throws MessagingException, IOException, InvalidFormatException {
        Exporter exporter;
        int choose;
        while (true) {
            printMenu();
            try {
                choose = input.nextInt();
            }
            catch (InputMismatchException exception) {
                System.out.println("Помилкове введення");
                input.nextLine();
                continue;
            }
            input.nextLine();
            switch (choose) {
                case 1:
                    openPlan();
                    break;
                case 2:
                    printExportOptions();
                    exporter = chooseExportOption();
                    if (exporter != null) {
                        sendAll(exporter);
                    }
                    break;
                case 3:
                    printExportOptions();
                    exporter = chooseExportOption();
                    if (exporter != null) {
                        sendOne(exporter);
                    }
                    break;
                case 4:
                    String path;
                    System.out.print("Шлях до папки куди експортувати: ");
                    path = input.next();
                    exportAll(path);
                    break;
                case 5:
                    printExportOptions();
                    exporter = chooseExportOption();
                    if (exporter != null) {
                        exportOne(exporter);
                    }
                    break;
                case 6:
                    sendOriginToAll();
                    break;
                case 7:
                    return;
                default:
                    input.nextLine();
                    break;
            }
        }
    }

    private void openPlan() throws IOException, InvalidFormatException {
        String sourcePath;
        System.out.print("Шлях до плану: ");
        sourcePath = input.nextLine();
        try {
            this.plan = new Plan(sourcePath);
            System.out.println("План відкрито");
        }
        catch (OldExcelFormatException exception) {
            System.out.println("Convert file to .xlsx");
            exception.printStackTrace();
        }
    }

    private void sendAll(Exporter exporter) throws MessagingException, IOException {
        System.out.println("SEND ALL START");
        System.out.println("MAILS:" + mails);
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        String exportPath;
        File file;
        for (Teacher teacher : teacherTablePlacement) {
            long startTime, stopTime;
            startTime = System.nanoTime();
            exportPath = "Plan.xlsx";
            System.out.println("Export Path:" + exportPath);
            file = new File(exportPath);
            System.out.println("File:" + file);
            exporter.export(teacher, exportPath);
            mailSender.setAttachment(file);
            System.out.println("teacher search mail: " + teacher.getCode());
            System.out.println("receiver: " + mails.get(teacher.getCode()));
            String mail = getTeacherMail(teacher);
            if (mail == null) {
                continue;
            }
            stopTime = System.nanoTime();
            System.out.println("Required time for table setup: " + (stopTime - startTime));
            mailSender.sendMessageAttachment(mail, "Your plan", "Here is your plan");
            file.delete();
            System.out.println("SEND!!");
        }
        System.out.println("Плани відправлено");
    }

    private void sendOne(Exporter exporter) throws MessagingException, IOException {
        String teacherCode;
        System.out.print("Код викладача: ");
        teacherCode = input.next();

        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();

        int i = searchTeacherByCode(teacherTablePlacement, teacherCode);
        if (i == -1) {
            System.out.println("Викладача з такими кодом не існує");
            return;
        }
        String exportPath = teacherTablePlacement.get(i).getCode() + ".xlsx";
        File file = new File(exportPath);
        exporter.export(teacherTablePlacement.get(i), exportPath);
        String mail = getTeacherMail(teacherTablePlacement.get(i));
        if (mail == null) {
            return;
        }
        mailSender.setAttachment(file);
        mailSender.sendMessageAttachment(mail, "Plan first semester", "Test send plan first semester");
        file.delete();
    }

    private void exportAll(String path) {
        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        Exporter exporter = new TeacherExporterYear(plan);
        for (Teacher teacher : teacherTablePlacement) {
            //TODO: make export .xls or .xlsx depending on origin file
            exporter.export(teacher, path + teacher.getCode() + ".xlsx");
        }
    }

    private void exportOne(Exporter exporter) {
        String teacherCode, exportPath;
        System.out.print("Код викладача: ");
        teacherCode = input.next();

        System.out.print("Шлях до нового файлу: ");
        exportPath = input.next();

        List<Teacher> teacherTablePlacement = plan.getTeacherTablePlacement();
        int i = searchTeacherByCode(teacherTablePlacement, teacherCode);
        if (i == -1) {
            System.out.println("Викладача з такими кодом не існує");
            return;
        }
        exporter.export(teacherTablePlacement.get(i), exportPath);
    }

    private void printMenu() {
        System.out.println("\t\t---МЕНЮ---");
        System.out.println("1. Відкрити план");
        System.out.println("2. Відправити всім викладачам (його план)");
        System.out.println("3. Відправити викладачу (його план)");
        System.out.println("4. Експортувати план всіх викладачів");
        System.out.println("5. Експортувати план викладача");
        System.out.println("6. Відправити весь план кожному викладачу");
        System.out.println("7. Вихід");
        System.out.print("> ");
    }

    private void printExportOptions() {
        System.out.println("1. Перший семестр");
        System.out.println("2. Другий семестр");
        System.out.println("3. Рік");
        System.out.print("> ");
    }

    private TeacherExporter chooseExportOption() {
        int choose = input.nextInt();
        input.nextLine();
        switch (choose) {
            case 1:
                return new TeacherExporterFirstSemester(plan);
            case 2:
                return new TeacherExporterSecondSemester(plan);
            case 3:
                return new TeacherExporterYear(plan);
            default:
                System.out.println("Помилка");
                return null;
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

    private int searchTeacherByCode(List<Teacher> teachers, String code) {
        for (int i = 0; i < teachers.size(); i++) {
            if (code.equals(teachers.get(i).getCode())) {
                return i;
            }
        }
        return -1;
    }

    private String getTeacherMail(Teacher teacher) {
        String mail = mails.get(teacher.getCode());
        if (mail == null) {
            System.out.println("Не вказано пошту викладача з кодом " + teacher.getCode());
            int choose;
            System.out.println("Подальша дія: ");
            System.out.println("1. Пропустити викладача");
            System.out.println("2. Ввести пошту вручну");
            System.out.print("> ");
            choose = ConsoleInput.getScanner().nextInt();
            switch (choose) {
                case 1:
                    return null;
                case 2:
                    System.out.print("Пошта викладача з кодом " + teacher.getCode() + ": ");
                    mail = ConsoleInput.getScanner().next();
                    return mail;
            }
        }
        return mail;
    }

    private void sendOriginToAll() throws MessagingException, IOException {
        List<Teacher> teachers = plan.getTeacherTablePlacement();
        mailSender.setAttachment(plan.getFile());
        String mail;
        for (Teacher teacher : teachers) {
            mail = getTeacherMail(teacher);
            mailSender.sendMessageAttachment(mail, "All plan", "Test send all");
        }
    }
}