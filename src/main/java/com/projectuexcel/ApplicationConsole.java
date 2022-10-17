package com.projectuexcel;

import com.projectuexcel.mail.MailSender;
import com.projectuexcel.util.ConsoleInput;
import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;
import com.projectuexcel.xls.export.*;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ApplicationConsole {
    Scanner input = ConsoleInput.getScanner();

    private MailSender mailSender;
    private XLSFilePlan plan;

    public ApplicationConsole() throws FileNotFoundException {
        this.mailSender = new MailSender();
    }

    public void run() throws MessagingException, IOException {
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
                    break;
                case 3:
                    sendOne();
                    break;
                case 4:
                    break;
                case 5:
                    printExportOptions();
                    Exporter exporter = chooseExportOption();
                    if (exporter != null) {
                        exportOne(exporter);
                    }
                    break;
                case 6:
                    return;
                default:
                    input.nextLine();
                    break;
            }
        }
    }

    private void openPlan() {
        String sourcePath;
        System.out.print("Шлях до плану: ");
        sourcePath = input.nextLine();
        try {
            this.plan = new XLSFilePlan(sourcePath);
            System.out.println("План відкрито");
        }
        catch (IOException exception) {
            this.plan = null;
            System.out.println("Файл не знайдено");
        }
    }

    private void sendAll() {

    }

    private void sendOne() throws MessagingException, IOException {
        printExportOptions();
        Exporter exporter = chooseExportOption();
        if (exporter == null) {
            return;
        }
        String teacherCode;
        System.out.print("Код викладача: ");
        teacherCode = input.next();
        String exportPath = "temp.xls";
        try {
            exporter.export(teacherCode, exportPath);
        }
        catch (CodeNotFoundException exception) {
            System.out.println("Викладача з такими кодом не існує");
            return;
        }
        mailSender.setAttachment(new File(exportPath));
        mailSender.sendMessageAttachment("maxym.sobol@gmail.com", "Plan", "Test sending plans");
    }

    private void exportAll() {

    }

    private void exportOne(Exporter exporter) {
        String teacherCode, exportPath;
        System.out.print("Код викладача: ");
        teacherCode = input.next();

        System.out.print("Шлях до нового файлу: ");
        exportPath = input.next();
        try {
            exporter.export(teacherCode, exportPath);
        }
        catch (CodeNotFoundException exception) {
            System.out.println("Викладача з такими кодом не існує");
        }
    }

    private void printMenu() {
        System.out.println("\t\t---МЕНЮ---");
        System.out.println("1. Відкрити план");
        System.out.println("2. Відправити всім викладачам (його план)");
        System.out.println("3. Відправити викладачу (його план)");
        System.out.println("4. Експортувати план всіх викладачів");
        System.out.println("5. Експортувати план викладача");
        System.out.println("6. Вихід");
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
}
