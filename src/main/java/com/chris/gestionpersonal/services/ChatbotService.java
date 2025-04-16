package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatbotService {

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;


    public ChatMessage chat(ChatMessage question) {
        ChatMessage chatMessage = new ChatMessage();
        ChatResponse chatResponse = retryPromptMessage(question);
        String response = chatResponse.getResult().getOutput().getText();
        chatMessage.setQuestion(response);
        return chatMessage;
    }

    public ByteArrayResource textToAudio(ChatMessage question) {
        log.info("ChatbotService: Received request to convert text to audio: {}", question.getQuestion());
        ChatMessage chatMessage = new ChatMessage();
        ChatResponse chatResponse = retryPromptMessage(question);
        String audioTranscript = chatResponse.getResult().getOutput().getText();
        log.info("ChatbotService: Received audio transcript: {}", audioTranscript);
        chatMessage.setQuestion(audioTranscript);
        byte[] generatedAudio = chatResponse.getResult().getOutput().getMedia().get(0).getDataAsByteArray();
        return new ByteArrayResource(generatedAudio);
    }

    private ChatResponse retryPromptMessage(ChatMessage question) {
        String promptContent = "Responde la pregunta correctamente teniendo en cuenta que pudo haber escrito incorrectamente , trata de interpretar lo que quiere decir ";
        String currentPrompt = "Responde a esta pregunta usando solo las columnas existentes en las tablas mencionadas:  " + question.getQuestion();
        String systemMessage = """
                    Eres un asistente que responde preguntas sobre una base de datos con las siguientes tablas:
                    - employee (id, full_name, email, role_id, status_id)
                    - role (id, name)
                    - status (id, name)
                    - assist (employee_id, date, entry_time, exit_time, worked_hours, incidents, reason)

                    Reglas de negocio:
                    - La columna 'incidents' en assist puede ser: ASISTENCIA (asistió a tiempo), RETARDO (asistió con retraso), FALTA (no asistió).
                    - RETARDO también cuenta como ASISTENCIA en conteos de asistencias.
                    - Usa COUNT() y GROUP BY para estadísticas.
                    - Para nombres con errores ortográficos (ej. 'Ana Martines' en lugar de 'Ana Martínez'), busca el nombre más similar usando ILIKE en PostgreSQL (ej. WHERE full_name ILIKE '%Ana Martinez%').

                    Instrucciones:
                    1. Genera una consulta SQL para validar los datos antes de responder.
                    2. Responde en lenguaje natural, siendo claro y conciso.
                    """;

            ChatResponse response = chatClient
                    .prompt(promptContent)
                    .system(systemMessage)
                    .user(currentPrompt)
                    .call()
                    .chatResponse();

            log.info("ChatbotService: Received response from AI: {}", response);

            return response;

        }

    public String audioToText() {
        var audioResource = new ClassPathResource("audioprueba2.mp3");
        OpenAiAudioTranscriptionOptions options =
                OpenAiAudioTranscriptionOptions.builder()
                        .language("es")
                        .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                        .temperature(0f)
                        .build();

        AudioTranscriptionPrompt prompt =
                new AudioTranscriptionPrompt(audioResource,options);

        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);
        String transcription = response.getResult().getOutput();
        return Normalizer.normalize(transcription, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}