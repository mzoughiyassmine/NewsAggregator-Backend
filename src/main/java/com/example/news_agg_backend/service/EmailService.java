package com.example.news_agg_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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
        message.setSubject("NewsAggregator : votre code de vérification");
        message.setText("""
                Bonjour,

                Votre code de vérification est : %s

                Il est valable 10 minutes. Si vous n'êtes pas à l'origine de cette demande, ignorez ce message.

                L'équipe NewsAggregator
                """.formatted(code));

        try {
            mailSender.send(message);
        } catch (MailException e) {
            // On ne consigne jamais le code, seulement le destinataire et la cause
            log.error("Échec de l'envoi du code de vérification à {} : {}", to, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Impossible d'envoyer l'e-mail de vérification. Réessayez plus tard.");
        }
    }
}