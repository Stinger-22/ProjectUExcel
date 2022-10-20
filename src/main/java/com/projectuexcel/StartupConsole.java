package com.projectuexcel;

import com.projectuexcel.table.exception.CodeNotFoundException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StartupConsole {
    public static void main(String[] args) throws MessagingException, IOException, InvalidFormatException {
        ApplicationConsole application;
        try {
            application = new ApplicationConsole();
        }
        catch (FileNotFoundException exception) {
            System.out.println("Файл codemail.txt не знайдено.");
            return;
        }
        application.run();
    }
}
