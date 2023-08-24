package com.pocan.pticket.services.mail;

import com.pocan.pticket.database.DatabaseOperation;
import com.pocan.pticket.pTicket;
import com.pocan.pticket.transcripts.TranscriptManager;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class MailService {

    final private static String from = "mail@mail.com";
    final private static String username = "mail@mail.com";
    final private static String password = "yourmailpassword";




    public static void sendMail(String recipientEmail, String ticketID){
        // Recipient's email ID needs to be mentioned.
        String to = recipientEmail;

        // Sender's email ID needs to be mentioned



        String host = "smtp.office365.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Creating database object

        DatabaseOperation databaseOperation = new DatabaseOperation();
        String ticketOwnerNickname = databaseOperation.getTicketOwnerNickname(ticketID);
        String ticketHTML = databaseOperation.getTicketHTML(ticketID);

        try {
            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Set Subject: header field
            message.setSubject("SAGANETWORK TELECOM. INC. | DESTEK SİSTEMİ");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("Merhaba " + databaseOperation.getTicketOwnerNickname(ticketID) + " , " + ticketID + " numaralı destek talebiniz sonlandırıldı.");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();

            File tempFile = TranscriptManager.getTranscriptFile(databaseOperation.getTicketHTML(ticketID));



            //String filename = "./pTicket/ticket-logs/" + ticketID + "/transcript.html";
            DataSource source = new FileDataSource(tempFile);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("talep-geçmişi.html");
            multipart.addBodyPart(messageBodyPart);

            // E-posta contentini ayarla.
            message.setContent(multipart);

            // E-postayı gönder.
            Transport.send(message);

            // Geçici oluşturulan dosyayı siliyoruz.
            tempFile.delete();
            String logger = "[" + pTicket.getSimpleDateFormat("HH:mm:ss") + " INFO]: " + ticketID + " numaralı destek talebine e-posta gönderildi.";
            pTicket.sendLogToConsole(logger);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
