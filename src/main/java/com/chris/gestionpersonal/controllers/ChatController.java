package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.ChatMessage;
import com.chris.gestionpersonal.services.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
public class ChatController {
    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody @Valid ChatMessage chatRequest) {
        log.info("ChatController: Received request with question: {}", chatRequest);
        ChatMessage answer = chatbotService.chat(chatRequest);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @GetMapping("/chat/audio")
    public ResponseEntity<ByteArrayResource> chatAudioStreamPreview() {
        String audioTranscription = chatbotService.audioToText();
        log.info("ChatController: Received audio transcription: {}", audioTranscription);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setQuestion(audioTranscription);
        ByteArrayResource byteArrayResource = chatbotService.textToAudio(chatMessage);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("pruebaaudio.mp3")
                                .build().toString())
                .body(byteArrayResource);
    }

}
