package com.projectuexcel;

import com.projectuexcel.util.ConsoleInput;
import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.CodeNotFoundException;
import com.projectuexcel.xls.export.Exporter;
import com.projectuexcel.xls.export.TeacherExporterYear;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main( String[] args ) throws IOException {
        XLSFilePlan plan = openPlan();
        if (plan == null) {
            return;
        }

        Exporter exporter = new TeacherExporterYear(plan);
        exportTeacherPlan(exporter);
    }

    public static XLSFilePlan openPlan() throws IOException {
        Scanner scanner = ConsoleInput.getScanner();

        String sourcePath;
        System.out.print("Шлях до плану: ");
        sourcePath = scanner.nextLine();

        XLSFilePlan plan;
        try {
            plan = new XLSFilePlan(sourcePath);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            return null;
        }
        return plan;
    }

    public static void exportTeacherPlan(Exporter exporter) {
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
}
