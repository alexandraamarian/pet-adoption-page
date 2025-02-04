package org.ubb.email_notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService
{
    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderService.class);

    @Value("${spring.mail.username}")
    private String emailAddress;

    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body)
    {
        LOG.info("Preparing email to: {}, subject: {}", to, subject);

        try
        {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom(emailAddress);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);

            LOG.info("Email successfully sent to: {}", to);
        } catch (MessagingException e)
        {
            LOG.error("Failed to send email to: {} - {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}