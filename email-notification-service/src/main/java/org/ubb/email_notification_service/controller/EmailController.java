package org.ubb.email_notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ubb.email_notification_service.service.EmailSenderService;

// This is just for debugging purposes
@RestController
@RequestMapping("/api/email")
public class EmailController
{
    private final EmailSenderService emailSenderService;

    public EmailController(EmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendTestEmail(@RequestParam("to") String to,
                                                @RequestParam("subject") String subject,
                                                @RequestParam("body") String body)
    {
        emailSenderService.sendEmail(to, subject, body);
        return ResponseEntity.ok("Email sent successfully to " + to);
    }
}