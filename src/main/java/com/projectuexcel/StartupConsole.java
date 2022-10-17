package com.projectuexcel;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StartupConsole {
    public static void main(String[] args) throws MessagingException, IOException {
        ApplicationConsole application;
        try {
            application = new ApplicationConsole();
        }
        catch (FileNotFoundException exception) {
            System.out.println("Файл password.txt або properties.txt не знайдено.");
            return;
        }
        application.run();
    }
}
