package com.projectuexcel.mail;

import java.io.IOException;

public class MainMail {
    public static void main(String[] args) throws IOException {
        MailSender mailSender = MailSender.getMailSender();
    }
}