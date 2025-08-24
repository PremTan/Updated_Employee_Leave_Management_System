package com.employee.elms.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.admin}")
    private String adminEmail;

    public void sendMail(String to, String subjet, String body){
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(adminEmail);
        mail.setTo(to);
        mail.setSubject(subjet);
        mail.setText(body);
        javaMailSender.send(mail);
    }

    public void notifyAdmin(String subject, String body) {
        sendMail(adminEmail, subject, body);
    }
}
