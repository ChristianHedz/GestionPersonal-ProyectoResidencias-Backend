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
}