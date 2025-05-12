package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.mapper.EmailMapper;
import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;
import com.chris.gestionpersonal.models.dto.EventType;
import com.chris.gestionpersonal.models.entity.CalendarEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService{
    @Value("${email.sender}")
    private String emailUser;

    private final JavaMailSender mailSender;
    private final EmailMapper emailMapper;

    @Override
    public EmailDTO sendEmailSimple(EmailDTO emailDTO) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(emailUser);
        mailMessage.setTo(emailDTO.getToUser());
        mailMessage.setSubject(emailDTO.getSubject());
        mailMessage.setText(emailDTO.getMessage());

        mailSender.send(mailMessage);
        return emailDTO;
    }

    @Override
    public EmailFileDTO sendEmailWithFile(EmailFileDTO emailFileDTO) {
        try {
            String fileName = emailFileDTO.getFile().getOriginalFilename();

            Path path = Paths.get("src/mail/resources/files/" + fileName);

            Files.createDirectories(path.getParent());
            Files.copy(emailFileDTO.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            File file = path.toFile();

            EmailDTO emailDTO = emailMapper.emailFileDtoToEmailDto(emailFileDTO);

            sendEmail(emailDTO, file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } 
            return emailFileDTO;

    }

public void sendEmail(EmailDTO emailDTO, File file) {
    try{
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

        mimeMessageHelper.setFrom(emailUser);
        mimeMessageHelper.setTo(emailDTO.getToUser());
        mimeMessageHelper.setSubject(emailDTO.getSubject());
        mimeMessageHelper.setText(emailDTO.getMessage(), true);
        if (file != null) {
            mimeMessageHelper.addAttachment(file.getName(), file);
        }

        // Better logging
        log.info("Sending email from: {}", emailUser);
        log.info("Sending email to: {}", String.join(", ", emailDTO.getToUser()));
        log.info("Email subject: {}", emailDTO.getSubject());

        mailSender.send(mimeMessage);
        log.info("Email sent successfully");
    } catch (MessagingException e) {
        throw new RuntimeException("Failed to send email", e);
    }
}

    public EmailDTO templateEmail(String email, String fullName) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setToUser(new String[]{email});
        emailDTO.setSubject("Bienvenido a GestionPersonal - Tu código QR de acceso");

        // Plantilla HTML mejorada
        String htmlTemplate =
                "<!DOCTYPE html>" +
                        "<html lang='es'>" +
                        "<head>" +
                        "    <meta charset='UTF-8'>" +
                        "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "    <title>Bienvenido a GestionPersonal</title>" +
                        "</head>" +
                        "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;'>" +
                        "    <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                        "        <div style='text-align: center; padding: 20px;'>" +
                        "            <h1 style='color: #2c3e50; margin-bottom: 20px; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>¡Bienvenido a GestionPersonal-Pasteleria Primavera</h1>" +
                        "        </div>" +
                        "        <div style='padding: 20px;'>" +
                        "            <p style='font-size: 16px; color: #2c3e50; margin-bottom: 20px;'>Hola <strong style='color: #3498db;'>" + fullName + "</strong>,</p>" +
                        "            <p style='font-size: 16px; color: #2c3e50; margin-bottom: 20px;'>Gracias por registrarte en nuestro sistema de gestión de personal. Estamos muy contentos de tenerte con nosotros.</p>" +
                        "            <div style='background-color: #f9f9f9; border-left: 4px solid #3498db; padding: 15px; margin: 20px 0;'>" +
                        "                <p style='font-size: 16px; color: #2c3e50; margin-bottom: 10px;'><strong>Tu código QR personal:</strong></p>" +
                        "                <p style='font-size: 16px; color: #2c3e50; margin-bottom: 0;'>Adjunto encontrarás tu código QR de acceso personal. Puedes utilizarlo para registrar tu asistencia en el sistema.</p>" +
                        "            </div>" +
                        "            <p style='font-size: 16px; color: #2c3e50; margin-top: 30px;'>Si tienes alguna duda, no dudes en contactarnos.</p>" +
                        "        </div>" +
                        "        <div style='text-align: center; background-color: #2c3e50; color: white; padding: 15px; border-radius: 0 0 5px 5px;'>" +
                        "            <p style='margin: 5px 0;'>GestionPersonal © " + java.time.Year.now().getValue() + "</p>" +
                        "            <p style='margin: 5px 0; font-size: 12px;'>Sistema de Gestión de Recursos Humanos</p>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";

        emailDTO.setMessage(htmlTemplate);
        return emailDTO;
    }

    @Override
    public EmailDTO sendBatchEventEmails(Map<String, String> emailsAndNames, CalendarEvent event) {
        log.info("entrando a metodo sendBatchEventEmails");
        EmailDTO emailDTO = new EmailDTO();

        // Convert map keys to array for toUser field
        String[] emails = emailsAndNames.keySet().toArray(new String[0]);
        emailDTO.setToUser(emails);

        // Set subject based on event type
        String subject = "Notificación de Evento: " + event.getTitle() + " - GestionPersonal";
        emailDTO.setSubject(subject);

        // Create HTML template with event details
        String htmlTemplate =
                "<!DOCTYPE html>" +
                        "<html lang='es'>" +
                        "<head>" +
                        "    <meta charset='UTF-8'>" +
                        "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "    <title>Notificación de Evento</title>" +
                        "    <link href='https://fonts.googleapis.com/css2?family=Montserrat:wght@400;600;700&display=swap' rel='stylesheet'>" +
                        "</head>" +
                        "<body style='margin: 0; padding: 0; font-family: \"Montserrat\", Arial, sans-serif; background-color: #f0f4f8;'>" +
                        "    <div style='max-width: 650px; margin: 30px auto; background-color: #ffffff; padding: 0; border-radius: 12px; box-shadow: 0 5px 15px rgba(0,0,0,0.1);'>" +
                        "        <div style='background-color: #1a73e8; padding: 30px; text-align: center; border-radius: 12px 12px 0 0;'>" +
                        "            <h1 style='color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 0.5px;'>Notificación de Evento</h1>" +
                        "        </div>" +
                        "        <div style='padding: 35px 30px;'>" +
                        "            <p style='font-size: 16px; color: #333; line-height: 1.5; margin-bottom: 20px;'>Estimado participante,</p>" +
                        "            <p style='font-size: 16px; color: #333; line-height: 1.5; margin-bottom: 25px;'>Has sido invitado al siguiente evento:</p>" +
                        "            <div style='background-color: #f8f9fa; border-left: 5px solid #1a73e8; padding: 25px; margin: 25px 0; border-radius: 4px;'>" +
                        "                <p style='font-size: 22px; color: #1a73e8; margin-top: 0; margin-bottom: 20px; font-weight: 600;'>" + event.getTitle() + "</p>" +
                        "                <table style='width: 100%; border-collapse: collapse;'>" +
                        "                    <tr>" +
                        "                        <td style='padding: 8px 0; width: 30%;'><strong style='color: #555;'>Tipo:</strong></td>" +
                        "                        <td style='padding: 8px 0; color: #333;'>" + getEventTypeInSpanish(event.getEventType()) + "</td>" +
                        "                    </tr>" +
                        "                    <tr>" +
                        "                        <td style='padding: 8px 0;'><strong style='color: #555;'>Fecha:</strong></td>" +
                        "                        <td style='padding: 8px 0; color: #333;'>" + formatDateTime(event.getStartDate()) +  formatDateTime(event.getEndDate()) + "</td>" +
                        "                    </tr>" +

                        "                    <tr>" +
                        "                        <td style='padding: 8px 0;'><strong style='color: #555;'>Descripción:</strong></td>" +
                        "                        <td style='padding: 8px 0; color: #333;'>" + (event.getDescription() != null ? event.getDescription() : "No hay descripción disponible") + "</td>" +
                        "                    </tr>" +
                        "                </table>" +
                        "            </div>" +
                        "            <p style='font-size: 16px; color: #333; line-height: 1.5; margin-top: 30px;'>Si tienes alguna duda, no dudes en contactarnos.</p>" +
                        "        </div>" +
                        "        <div style='text-align: center; background-color: #2c3e50; color: white; padding: 20px; border-radius: 0 0 12px 12px;'>" +
                        "            <img src='https://via.placeholder.com/120x30/2c3e50/ffffff?text=GestionPersonal' alt='Logo' style='margin-bottom: 15px;'>" +
                        "            <p style='margin: 5px 0; font-size: 14px;'>© " + java.time.Year.now().getValue() + " GestionPersonal</p>" +
                        "            <p style='margin: 5px 0; font-size: 12px; color: #ccc;'>Sistema de Gestión de Recursos Humanos</p>" +
                        "            <div style='margin-top: 15px;'>" +
                        "                <a href='#' style='color: white; margin: 0 10px; text-decoration: none; font-size: 13px;'>Contáctanos</a> | " +
                        "                <a href='#' style='color: white; margin: 0 10px; text-decoration: none; font-size: 13px;'>Política de Privacidad</a>" +
                        "            </div>" +
                        "        </div>" +
                        "    </div>" +
                        "</body>" +
                        "</html>";

        emailDTO.setMessage(htmlTemplate);

        // Send the email
        sendEmail(emailDTO,null);
        log.info("Batch event emails sent successfully to: {}", String.join(", ", emailsAndNames.keySet()));
        return emailDTO;
    }

    // Helper method to format LocalDateTime
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "No especificada";

        // Format: "dd/MM/yyyy HH:mm"
        return dateTime.getDayOfMonth() + "/" + 
               dateTime.getMonthValue() + "/" + 
               dateTime.getYear() + " " + 
               String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }

    // Helper method to translate event types to Spanish
    private String getEventTypeInSpanish(EventType eventType) {
        if (eventType == null) return "No especificado";

        switch (eventType) {
            case MEETING:
                return "Reunión";
            case VACATION:
                return "Vacaciones";
            case REMINDER:
                return "Recordatorio";
            case OTHER:
                return "Otro";
            default:
                return "No especificado";
        }
    }


}
