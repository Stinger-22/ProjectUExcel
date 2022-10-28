package com.projectuexcel.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class MailSender {
    private static MailSender mailSender;

    private String senderMail = "kartku.navantazennia@gmail.com";
    private Properties properties;
    private Session session;

    private File attachment;

    private MailSender() throws FileNotFoundException {
        properties = getProperties();
        String password = getPassword();

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderMail, password);
            }
        };

        session = Session.getInstance(properties, authenticator);
    }

    public static MailSender getMailSender() throws FileNotFoundException {
        if (mailSender == null) {
            mailSender = new MailSender();
        }
        return mailSender;
    }

    private String getPassword() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("password.txt"));
        return scanner.next();
    }

    private Properties getProperties() throws FileNotFoundException {
        properties = new Properties();
        Scanner scanner = new Scanner(new File("properties.txt"));
        String[] props;
        for (int i = 0; i < 2; i++) {
            props = scanner.nextLine().split("=");
            properties.put(props[0], Boolean.parseBoolean(props[1]));
        }
        for (int i = 0; i < 4; i++) {
            props = scanner.nextLine().split("=");
            properties.put(props[0], props[1]);
        }
        return properties;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    public void sendMessageAttachment(String receiver, String subject, String text) throws MessagingException, IOException {
        if (attachment == null) {
            throw new IllegalStateException("No attachment file is set. Use method setAttachment");
        }
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderMail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));

        //TODO: try use multithreading to increase speed?
        sendMessage(message, subject, text);
    }

    public void sendMessageAttachment(String[] receivers, String subject, String text) throws MessagingException, IOException {
        if (attachment == null) {
            throw new IllegalStateException("No attachment file is set. Use method setAttachment");
        }
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderMail));
        for (String receiver : receivers) {
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        }

        //TODO: try use multithreading to increase speed?
        sendMessage(message, subject, text);
    }

    private void sendMessage(Message message, String subject, String text) throws MessagingException, IOException {
        message.setSubject(subject);
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(attachment);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(text, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);
        Transport.send(message);
    }
}
