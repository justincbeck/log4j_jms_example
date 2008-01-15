package com.beckproduct.service;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * This class is used to send notifications about new exceptions
 * 
 * @author jbeck
 */
public class EmailService implements IEmailService
{
    private Logger logger = Logger.getLogger(this.getClass());
    
    private JavaMailSender mailSender;

    /**
     * @see com.beckproduct.service.IEmailService#sendNotification(int, int)
     */
    public void sendNotification(int nonNotified, int nonReviewed)
    {
        try
        {
            InternetAddress sender = new InternetAddress("exceptions@meddius.com");
            InternetAddress recipient = new InternetAddress("justin.beck@intalgent.com");

            MimeMessage message = mailSender.createMimeMessage();

            message.setSubject("Meddius: Exception Notification!");
            message.setFrom(sender);
            message.setRecipient(RecipientType.TO, recipient);
            message.setSentDate(new Date());
            StringBuffer content = new StringBuffer();
            content.append("New Exceptions have been captured:\n\n");
            content.append("There are " + nonNotified + " new Exceptions since the last report email.\n");
            content.append("There are " + nonReviewed + " Exceptions that have not been reviewed.\n\n");
            content.append("Message generated at: " + new Date());
            message.setContent(content.toString(), "text/plain");
            mailSender.send(message);
        }
        catch (AddressException e)
        {
            logger.error(e.getMessage(), e);
        }
        catch (MessagingException e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @return the mailSender
     */
    public JavaMailSender getMailSender()
    {
        return mailSender;
    }

    /**
     * @param mailSender
     *            the mailSender to set
     */
    public void setMailSender(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }
}
