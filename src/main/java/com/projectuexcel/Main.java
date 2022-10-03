package com.projectuexcel;

import com.projectuexcel.xls.XLSFilePlan;
import com.projectuexcel.xls.exception.TeacherNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main( String[] args ) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String sourcePath;
        System.out.print("Шлях до плану: ");
        sourcePath = scanner.nextLine();

        XLSFilePlan plan;
        try {
            plan = new XLSFilePlan(sourcePath);
        }
        catch (FileNotFoundException exception) {
            System.out.println(exception);
            return;
        }

        String initials, exportPath;
        System.out.print("Ініціали викладача: ");
        initials = scanner.next();

        System.out.print("Шлях до нового файлу: ");
        exportPath = scanner.next();
        try {
            plan.exportTeacherPlan(initials, exportPath);
        }
        catch (TeacherNotFoundException exception) {
            System.out.println(exception);
        }
    }
}
