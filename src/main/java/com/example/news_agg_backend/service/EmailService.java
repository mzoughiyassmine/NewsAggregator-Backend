package com.example.news_agg_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;


    public void sendRegistrationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Newsly : votre code de vérification");
        message.setText("Votre code de vérification est : " + code
                + "\n\nIl est valable 10 minutes."
                + "\nSi vous n'avez pas demandé ce code, ignorez ce message.");
        mailSender.send(message);
    }
}