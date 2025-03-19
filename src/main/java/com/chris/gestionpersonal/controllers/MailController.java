package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;
import com.chris.gestionpersonal.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;

    @PostMapping("/sendMessage")
    public ResponseEntity<EmailDTO> receiveRequestEmail(@RequestBody EmailDTO emailDTO){
        emailService.sendEmailSimple(emailDTO);
        return new ResponseEntity<>(emailDTO, HttpStatus.OK);
    }

    @PostMapping("/sendMessageFile")
    public ResponseEntity<EmailFileDTO> receiveRequestEmailWithFile(@ModelAttribute EmailFileDTO emailFileDTO){
        EmailFileDTO emailFileDTOResponse = emailService.sendEmailWithFile(emailFileDTO);
        return new ResponseEntity<>(emailFileDTOResponse, HttpStatus.OK);
    }
}