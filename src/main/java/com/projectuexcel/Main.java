package com.projectuexcel;

import com.projectuexcel.util.ConsoleInput;
import com.projectuexcel.table.Plan;
import com.projectuexcel.table.exception.CodeNotFoundException;
import com.projectuexcel.table.export.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main( String[] args ) {
        System.out.println("Hello World");
    }

    public static Plan openPlan() throws IOException, InvalidFormatException {
        Scanner scanner = ConsoleInput.getScanner();

        String sourcePath;
        System.out.print("Шлях до плану: ");
        sourcePath = scanner.nextLine();

        Plan plan;
        plan = new Plan(sourcePath);
        return plan;
    }
}
