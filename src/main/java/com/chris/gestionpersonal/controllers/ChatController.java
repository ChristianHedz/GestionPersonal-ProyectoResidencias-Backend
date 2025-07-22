package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.ChatMessage;
import com.chris.gestionpersonal.services.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Chatbot", description = "Endpoints para interacción con el chatbot")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ChatController {
    private final ChatbotService chatbotService;

    @Operation(summary = "Chat con texto", description = "Envía un mensaje de texto al chatbot y recibe una respuesta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta del chatbot obtenida exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Mensaje inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody @Valid ChatMessage chatRequest) {
        log.info("ChatController: Received request with question: {}", chatRequest);
        ChatMessage answer = chatbotService.chat(chatRequest);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @Operation(summary = "Chat con audio", description = "Envía un archivo de audio al chatbot para transcripción y respuesta en audio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta de audio generada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Archivo de audio inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/chat/audio")
    public ResponseEntity<byte[]> chatAudioStreamPreview(@RequestParam("audio") MultipartFile audioFile) {
        log.info("ChatController: Received audio file for transcription: {}", audioFile.getOriginalFilename());
        String audioTranscription = chatbotService.audioToText(audioFile);
        log.info("ChatController: Received audio transcription: {}", audioTranscription);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setQuestion(audioTranscription);
        byte[] audioResponse = chatbotService.textToAudio(chatMessage);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audioResponse);
    }

    @Operation(summary = "Procesar documento FAQ", description = "Procesa un documento para generar respuestas FAQ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documento procesado exitosamente",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Documento inválido"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "Security Token")
    @PostMapping("/chat/faq")
    public ResponseEntity<ChatMessage> chatFaq(@RequestParam("document") MultipartFile document) {
        log.info("ChatController: Received document for FAQ processing: {}", document.getOriginalFilename());
        // For now, we'll create a simple response
        // In a real implementation, you would process the document and generate a response
        ChatMessage response = new ChatMessage();
        response.setQuestion("Documento recibido: " + document.getOriginalFilename());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
