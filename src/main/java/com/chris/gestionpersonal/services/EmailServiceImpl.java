package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.mapper.EmailMapper;
import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

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
        mimeMessageHelper.addAttachment(file.getName(), file);

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


}