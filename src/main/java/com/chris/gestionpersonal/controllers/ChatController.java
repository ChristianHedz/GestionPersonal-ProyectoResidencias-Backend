package com.chris.gestionpersonal.controllers;

import com.chris.gestionpersonal.models.dto.ChatMessage;
import com.chris.gestionpersonal.services.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> chat(@RequestBody ChatMessage chatRequest) {
            log.info("ChatController: Received request with question: {}", chatRequest);
            ChatMessage answer = chatbotService.chat(chatRequest);
            return new ResponseEntity<>(answer, HttpStatus.OK);
    }

}