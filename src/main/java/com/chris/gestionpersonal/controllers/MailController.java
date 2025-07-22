package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.EmailDTO;
import com.chris.gestionpersonal.models.dto.EmailFileDTO;
import com.chris.gestionpersonal.services.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Email Service", description = "Endpoints para envío de correos electrónicos")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class MailController {

    private final EmailService emailService;

    @Operation(summary = "Enviar email simple", description = "Envía un correo electrónico simple sin archivos adjuntos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email enviado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmailDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Datos del email inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/sendMessage")
    public ResponseEntity<EmailDTO> receiveRequestEmail(@RequestBody EmailDTO emailDTO){
        emailService.sendEmailSimple(emailDTO);
        return new ResponseEntity<>(emailDTO, HttpStatus.OK);
    }

    @Operation(summary = "Enviar email con archivo", description = "Envía un correo electrónico con archivos adjuntos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email con archivo enviado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EmailFileDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Datos del email o archivo inválidos"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/sendMessageFile")
    public ResponseEntity<EmailFileDTO> receiveRequestEmailWithFile(@ModelAttribute EmailFileDTO emailFileDTO){
        EmailFileDTO emailFileDTOResponse = emailService.sendEmailWithFile(emailFileDTO);
        return new ResponseEntity<>(emailFileDTOResponse, HttpStatus.OK);
    }
}