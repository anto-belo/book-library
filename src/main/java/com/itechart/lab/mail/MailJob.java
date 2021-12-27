package com.itechart.lab.mail;

import com.itechart.lab.dto.MailBorrow;
import com.itechart.lab.exception.ServiceException;
import com.itechart.lab.service.BorrowService;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

@Log4j2
public class MailJob implements Job {
    private static final String MAIL_SMTP_HOST = "smtp.gmail.com";
    private static final String MAIL_SMTP_PORT = "465";
    private static final String AUTH_PASS = "dafjyxarjfwzjyvn";
    private static final String FROM_EMAIL = "ifreeman9015@gmail.com";

    private static final String MAIL_SUBJECT_NOTIFY_BEFORE = "Don't forget to return a book!";
    private static final String MAIL_SUBJECT_NOTIFY_AFTER = "You have a debt in the library!";

    private static void mail(MailBorrow borrow, String dueDate, boolean notifyBefore) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", MAIL_SMTP_HOST);
        properties.put("mail.smtp.port", MAIL_SMTP_PORT);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, AUTH_PASS);
            }
        });

//        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(borrow.getReaderEmail()));

            String messageSubject = notifyBefore ? MAIL_SUBJECT_NOTIFY_BEFORE
                    : MAIL_SUBJECT_NOTIFY_AFTER;
            message.setSubject(messageSubject);

            String mailText = MailTemplate.template(notifyBefore, borrow.getReaderName(),
                    borrow.getBookTitle(), dueDate);
            message.setText(mailText);

            Transport.send(message);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            String dueDateInWeek = getFormattedDateNowPlusDays(7);
            BorrowService.getInstance()
                    .findBorrowsExpiringOn(dueDateInWeek).stream()
                    .map(MailBorrow::new)
                    .filter(b -> b.getReaderEmail() != null)
                    .forEach(b -> mail(b, dueDateInWeek, true));

            String dueDateTomorrow = getFormattedDateNowPlusDays(1);
            BorrowService.getInstance()
                    .findBorrowsExpiringOn(dueDateTomorrow).stream()
                    .map(MailBorrow::new)
                    .filter(b -> b.getReaderEmail() != null)
                    .forEach(b -> mail(b, dueDateTomorrow, true));

            String dueDateYesterday = getFormattedDateNowPlusDays(-1);
            BorrowService.getInstance()
                    .findBorrowsExpiringOn(dueDateYesterday).stream()
                    .map(MailBorrow::new)
                    .filter(b -> b.getReaderEmail() != null)
                    .forEach(b -> mail(b, dueDateYesterday, false));
        } catch (ServiceException e) {
            log.error(e.getMessage());
        }
    }

    private String getFormattedDateNowPlusDays(int days) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, days);
        return dateFormat.format(c.getTime());
    }
}
