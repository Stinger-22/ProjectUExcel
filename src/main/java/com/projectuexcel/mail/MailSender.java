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
    private String senderMail = "max.botprog@gmail.com";
    private Properties properties;
    private Session session;

    private File attachment;

    public MailSender() throws FileNotFoundException {
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

    public void sendMessageAttachment(String receiver) throws MessagingException, IOException {
        if (attachment == null) {
            throw new IllegalStateException("No attachment file is set. Use method setAttachment");
        }
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderMail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        message.setSubject("Plan");

        String msg = "Here is your plan";

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(attachment);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}