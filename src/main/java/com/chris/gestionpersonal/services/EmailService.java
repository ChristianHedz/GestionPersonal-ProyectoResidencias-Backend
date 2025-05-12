package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;
import com.chris.gestionpersonal.models.entity.CalendarEvent;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface EmailService {
    EmailDTO sendEmailSimple(EmailDTO emailDTO);
    EmailFileDTO sendEmailWithFile(EmailFileDTO emailFileDTO);
    void sendEmail(EmailDTO emailDTO, File file);
    EmailDTO templateEmail(String email, String fullName);
    EmailDTO sendBatchEventEmails(Map<String, String> emailsAndNames, CalendarEvent event);
}
