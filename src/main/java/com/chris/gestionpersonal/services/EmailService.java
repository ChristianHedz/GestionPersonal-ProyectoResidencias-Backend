package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;

import java.io.File;

public interface EmailService {
    EmailDTO sendEmailSimple(EmailDTO emailDTO);
    EmailFileDTO sendEmailWithFile(EmailFileDTO emailFileDTO);
    void sendEmail(EmailDTO emailDTO, File file);
}