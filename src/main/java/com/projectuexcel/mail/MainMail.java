package com.projectuexcel.mail;

import javax.mail.*;
import java.io.File;
import java.io.IOException;

public class MainMail {
    public static void main(String[] args) throws IOException {
        MailSender mailSender = MailSender.getMailSender();
    }
}