package com.suryakn.IssueTracker.service;

import com.suryakn.IssueTracker.entity.Ticket;
import com.suryakn.IssueTracker.entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:80}")
    private String baseUrl;

    public void sendTicketCreatedNotification(Ticket ticket, UserEntity assignee) {
        if (assignee == null || assignee.getEmail() == null) {
            log.warn("Assignee email missing, skipping ticket created notification");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(assignee.getEmail());
            helper.setSubject("[Air Algérie] Ticket #" + ticket.getId() + " créé - " + ticket.getTitle());

            String content = buildTicketCreatedEmail(ticket);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Ticket creation notification sent to {} for ticket {}", assignee.getEmail(), ticket.getId());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", assignee.getEmail(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email: {}", e.getMessage(), e);
        }
    }

    public void sendTicketAssignedNotification(Ticket ticket, UserEntity assignee) {
        if (assignee == null || assignee.getEmail() == null) {
            log.warn("Assignee email missing, skipping assignment notification");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(assignee.getEmail());
            helper.setSubject("[Air Algérie] Ticket #" + ticket.getId() + " assigné - " + ticket.getTitle());

            String content = buildTicketAssignedEmail(ticket);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Ticket assignment notification sent to {} for ticket {}", assignee.getEmail(), ticket.getId());
        } catch (MessagingException e) {
            log.error("Failed to send assignment email to {}: {}", assignee.getEmail(), e.getMessage(), e);
        }
    }

    private String buildTicketCreatedEmail(Ticket ticket) {
        String projectName = (ticket.getProject() != null) ? ticket.getProject().getName() : "N/A";
        String deptName = (ticket.getProject() != null && ticket.getProject().getDepartment() != null)
                ? ticket.getProject().getDepartment().getName()
                : "N/A";

        return """
                <html>
                <body style='font-family: Arial, sans-serif;'>
                    <h2>🎫 Nouveau ticket créé - Air Algérie</h2>
                    <p>Un nouveau ticket a été créé dans le système de gestion des demandes.</p>
                    <table style='border-collapse: collapse; width: 100%; margin: 10px 0;'>
                        <tr>
                            <td><strong>ID Ticket:</strong></td>
                            <td>#%d</td>
                        </tr>
                        <tr>
                            <td><strong>Titre:</strong></td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td><strong>Description:</strong></td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td><strong>Catégorie:</strong></td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td><strong>Priorité:</strong></td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td><strong>Projet:</strong></td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td><strong>Service:</strong></td>
                            <td>%s</td>
                        </tr>
                    </table>
                    <p>Consultez le ticket dans le portail: <a href="%s/projects/%d/tickets/%d">%s/projects/%d/tickets/%d</a></p>
                    <hr>
                    <p style='color: #666; font-size: 0.9em;'>Cet email a été généré automatiquement par la plateforme interne Air Algérie.</p>
                </body>
                </html>
                """.formatted(
                ticket.getId(),
                ticket.getTitle(),
                stripHtml(ticket.getDescription()),
                ticket.getCategory(),
                ticket.getPriority(),
                projectName,
                deptName,
                baseUrl, ticket.getProject() != null ? ticket.getProject().getId() : 0, ticket.getId(),
                baseUrl, ticket.getProject() != null ? ticket.getProject().getId() : 0, ticket.getId()
        );
    }

    private String buildTicketAssignedEmail(Ticket ticket) {
        return """
                <html>
                <body style='font-family: Arial, sans-serif;'>
                    <h2>📌 Ticket assigné - Air Algérie</h2>
                    <p>Le ticket suivant vous a été assigné:</p>
                    <table style='border-collapse: collapse; width: 100%; margin: 10px 0;'>
                        <tr>
                            <td><strong>ID Ticket:</strong></td>
                            <td>#%d</td>
                        </tr>
                        <tr>
                            <td><strong>Titre:</strong></td>
                            <td>%s</td>
                        </tr>
                        <tr>
                            <td><strong>Description:</strong></td>
                            <td>%s</td>
                        </tr>
                    </table>
                    <p><a href="%s/projects/%d/tickets/%d">Accéder au ticket</a></p>
                </body>
                </html>
                """.formatted(
                ticket.getId(),
                ticket.getTitle(),
                stripHtml(ticket.getDescription()),
                baseUrl, ticket.getProject() != null ? ticket.getProject().getId() : 0, ticket.getId()
        );
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
    }

    public void sendPasswordResetEmail(String email, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("[Air Algérie] Réinitialisation de votre mot de passe");

        String content = buildPasswordResetEmail(token);
        helper.setText(content, true);

        mailSender.send(message);
        log.info("Password reset email sent to {}", email);
    }

    private String buildPasswordResetEmail(String token) {
        String resetLink = baseUrl + "/reset-password?token=" + token;
        return """
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <h2>🔐 Réinitialisation de mot de passe - Air Algérie</h2>
                <p>Vous avez demandé la réinitialisation de votre mot de passe.</p>
                <p>Cliquez sur le lien ci-dessous pour créer un nouveau mot de passe:</p>
                <p style='margin: 20px 0;'>
                    <a href='%s' style='background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;'>
                        Réinitialiser mon mot de passe
                    </a>
                </p>
                <p>Ou copiez ce lien dans votre navigateur: %s</p>
                <p style='color: #666; font-size: 0.9em; margin-top: 20px;'>
                    <strong>Attention:</strong> Ce lien expire dans 1 heure. Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.
                </p>
                <hr>
                <p style='color: #666; font-size: 0.8em;'>Cet email a été généré automatiquement par la plateforme interne Air Algérie.</p>
            </body>
            </html>
            """.formatted(resetLink, resetLink);
    }
}
