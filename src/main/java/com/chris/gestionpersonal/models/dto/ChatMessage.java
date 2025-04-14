package com.chris.gestionpersonal.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ChatMessage {
    @NotBlank(message = "{chatbot.message.notBlank}")
    private String question;
}
