package com.ecodeli.ecodeli_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecodeli.ecodeli_backend.models.Utilisateur;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${MAIL_FROM}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(Utilisateur utilisateur) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "EcoDeli");
            helper.setTo(utilisateur.getEmail());
            helper.setSubject("Bienvenue chez EcoDeli ! 🌱");

            String htmlContent = buildWelcomeEmailContent(utilisateur);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de bienvenue envoyé avec succès à : {}", utilisateur.getEmail());

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de bienvenue à {} : {}",
                        utilisateur.getEmail(), e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'envoi de l'email de bienvenue à {} : {}",
                        utilisateur.getEmail(), e.getMessage());
        }
    }

    private String buildWelcomeEmailContent(Utilisateur utilisateur) {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Bienvenue chez EcoDeli</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f9f9f9;
                    }
                    .container {
                        background-color: #ffffff;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        padding-bottom: 20px;
                        border-bottom: 2px solid #4CAF50;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 32px;
                        font-weight: bold;
                        color: #4CAF50;
                        margin-bottom: 10px;
                    }
                    .welcome-title {
                        color: #2E7D32;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .content {
                        font-size: 16px;
                        margin-bottom: 20px;
                    }
                    .highlight {
                        background-color: #E8F5E8;
                        padding: 15px;
                        border-radius: 5px;
                        border-left: 4px solid #4CAF50;
                        margin: 20px 0;
                    }
                    .footer {
                        margin-top: 30px;
                        padding-top: 20px;
                        border-top: 1px solid #e0e0e0;
                        font-size: 14px;
                        color: #666;
                        text-align: center;
                    }
                    .contact-info {
                        background-color: #f5f5f5;
                        padding: 15px;
                        border-radius: 5px;
                        margin-top: 20px;
                    }
                    .emoji {
                        font-size: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🌱 EcoDeli</div>
                        <p style="color: #666; margin: 0;">Votre plateforme éco-responsable</p>
                    </div>

                    <h1 class="welcome-title">Bonjour %s ! <span class="emoji">👋</span></h1>

                    <div class="content">
                        <p>Nous sommes ravis de vous accueillir dans la communauté <strong>EcoDeli</strong> !</p>

                        <div class="highlight">
                            <p><strong>🎉 Votre compte a été créé avec succès !</strong></p>
                            <p>Vous pouvez maintenant profiter de tous nos services éco-responsables pour une livraison plus verte et durable.</p>
                        </div>

                        <p>Avec EcoDeli, vous contribuez à :</p>
                        <ul>
                            <li><span class="emoji">🌍</span> Réduire l'impact environnemental des livraisons</li>
                            <li><span class="emoji">🚴</span> Promouvoir des modes de transport écologiques</li>
                            <li><span class="emoji">📦</span> Optimiser les circuits de livraison</li>
                            <li><span class="emoji">🤝</span> Soutenir l'économie locale</li>
                        </ul>

                        <p>N'hésitez pas à explorer notre plateforme et à découvrir toutes les fonctionnalités disponibles.</p>
                    </div>

                    <div class="contact-info">
                        <h3 style="color: #2E7D32; margin-top: 0;">📞 Besoin d'aide ?</h3>
                        <p>Notre équipe est là pour vous accompagner :</p>
                        <p>
                            <strong>Email :</strong> ecodeli.nepasrepondre@gmail.com<br>
                            <strong>Plateforme :</strong> <a href="#" style="color: #4CAF50;">ecodeli.com</a>
                        </p>
                    </div>

                    <div class="footer">
                        <p>Merci de faire confiance à EcoDeli pour vos livraisons éco-responsables !</p>
                        <p style="margin-top: 15px;">
                            <strong>L'équipe EcoDeli</strong> 🌱
                        </p>
                        <hr style="margin: 20px 0; border: none; border-top: 1px solid #e0e0e0;">
                        <p style="font-size: 12px; color: #999;">
                            Cet email a été envoyé automatiquement, merci de ne pas y répondre.<br>
                            © 2025 EcoDeli - Tous droits réservés
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(utilisateur.getPrenom());
    }
}
