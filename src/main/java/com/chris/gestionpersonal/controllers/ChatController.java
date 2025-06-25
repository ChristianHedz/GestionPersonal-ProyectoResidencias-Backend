package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.ChatMessage;
import com.chris.gestionpersonal.services.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ChatController {
    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody @Valid ChatMessage chatRequest) {
        log.info("ChatController: Received request with question: {}", chatRequest);
        ChatMessage answer = chatbotService.chat(chatRequest);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

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
