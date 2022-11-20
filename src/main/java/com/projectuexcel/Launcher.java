package com.projectuexcel;

import com.projectuexcel.ui.ApplicationWindow;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException {
//        Desktop.getDesktop().open(new File("Book1.xlsx"));
        ApplicationWindow.main(args);
    }
}
