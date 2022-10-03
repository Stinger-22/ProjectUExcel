package com.projectuexcel;

import com.projectuexcel.util.ConsoleInput;
import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.TeacherNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main( String[] args ) throws IOException {
        XLSFilePlan plan = openPlan();
        if (plan == null) {
            return;
        }

        exportTeacherPlan(plan);
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

    public static void exportTeacherPlan(XLSFilePlan origin) {
        Scanner scanner = ConsoleInput.getScanner();

        String initials, exportPath;
        System.out.print("Ініціали викладача: ");
        initials = scanner.next();

        System.out.print("Шлях до нового файлу: ");
        exportPath = scanner.next();
        try {
            origin.exportTeacherPlan(initials, exportPath);
        }
        catch (TeacherNotFoundException exception) {
            exception.printStackTrace();
        }
    }
}
