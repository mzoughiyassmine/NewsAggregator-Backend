
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

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendResetCode(String recipientEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Newsly - Réinitialisation du mot de passe");
        message.setText(
                "Bonjour,\n\n" +
                        "Vous avez demandé la réinitialisation de votre mot de passe Newsly.\n\n" +
                        "Votre code de vérification est : " + code + "\n\n" +
                        "Ce code expire dans 10 minutes.\n\n" +
                        "Si vous n'avez pas effectué cette demande, ignorez cet e-mail.\n\n" +
                        "L'équipe Newsly"
        );

        mailSender.send(message);
    }

    public void sendRegistrationCode(String recipientEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Newsly - Vérification de votre adresse e-mail");
        message.setText(
                "Bonjour,\n\n" +
                        "Merci de vouloir rejoindre Newsly !\n\n" +
                        "Votre code de vérification est : " + code + "\n\n" +
                        "Ce code expire dans 10 minutes.\n\n" +
                        "Saisissez ce code dans l'application pour confirmer votre adresse e-mail.\n\n" +
                        "L'équipe Newsly"
        );
        mailSender.send(message);
    }
}
