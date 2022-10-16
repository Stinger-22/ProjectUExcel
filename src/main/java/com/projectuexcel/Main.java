package com.projectuexcel;

import com.projectuexcel.util.ConsoleInput;
import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;
import com.projectuexcel.xls.export.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main( String[] args ) throws IOException {
        Map<String, String> init = readIniFile();
        fileExportTeacherPlan(init);

//        XLSFilePlan plan = openPlan();
//        if (plan == null) {
//            return;
//        }

//        Exporter exporter = new TeacherExporterSecondSemester(plan);
//        consoleExportTeacherPlan(exporter);
    }

    public static XLSFilePlan openPlan() throws IOException {
        Scanner scanner = ConsoleInput.getScanner();

        String sourcePath;
        System.out.print("Шлях до плану: ");
        sourcePath = scanner.nextLine();

        XLSFilePlan plan;
        plan = new XLSFilePlan(sourcePath);
        return plan;
    }

    public static void consoleExportTeacherPlan(Exporter exporter) {
        Scanner scanner = ConsoleInput.getScanner();

        String teacherCode, exportPath;
        System.out.print("Код викладача: ");
        teacherCode = scanner.next();

        System.out.print("Шлях до нового файлу: ");
        exportPath = scanner.next();
        try {
            exporter.export(teacherCode, exportPath);
        }
        catch (CodeNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public static void fileExportTeacherPlan(Map<String, String> init) throws IOException {
        Exporter exporter;
        String teacherCode, exportPath;
        XLSFilePlan plan;
        plan = new XLSFilePlan(System.getProperty("user.dir") + "\\" + init.get("origin"));

        teacherCode = init.get("teacher");
        exportPath = init.get("output");

        switch (init.get("semester")) {
            case "1":
                exporter = new TeacherExporterFirstSemester(plan);
                break;
            case "2":
                exporter = new TeacherExporterSecondSemester(plan);
                break;
            case "3":
                exporter = new TeacherExporterYear(plan);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + init.get("semester"));
        }
        try {
            exporter.export(teacherCode, exportPath);
        }
        catch (CodeNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    // TODO: check if retrieved data/ini file is valid?
    public static Map<String, String> readIniFile() {
        Map<String, String> init = new HashMap<>();
        File file = new File("setup.ini");
        String buffer;
        String[] filtered;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                filtered = scanner.nextLine().split("=");
                init.put(filtered[0].trim(), filtered[1].trim());
            }
        }
        catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
        return init;
    }
}
