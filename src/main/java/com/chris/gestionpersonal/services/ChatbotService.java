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
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatbotService {

    private final ChatClient chatClient;
    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    public ChatMessage chat(ChatMessage question) {
        ChatResponse response = processChatRequest(question, false);
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            ChatMessage errorMessage = new ChatMessage();
            errorMessage.setQuestion("Lo siento, no pude procesar tu solicitud en este momento. Por favor, intenta de nuevo.");
            return errorMessage;
        }
        String responseText = response.getResult().getOutput().getText();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setQuestion(responseText);
        return chatMessage;
    }

    public byte[] textToAudio(ChatMessage question) {
        log.info("ChatbotService: Received request to convert text to audio: {}", question.getQuestion());
        ChatResponse response = processChatRequest(question, true);
        log.info("ChatbotService: Received response from AI: {}", response);

        String audioTranscript = response.getResult().getOutput().getText();
        log.info("ChatbotService: Received audio transcript: {}", audioTranscript);

        return response.getResult().getOutput().getMedia().getFirst().getDataAsByteArray();
    }

    private ChatResponse processChatRequest(ChatMessage question, boolean withAudio) {
        Map<String, String> chatResponse = promptsMessage(question);

        OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder()
                .temperature(1.0);

        if (withAudio) {
            optionsBuilder.model(OpenAiApi.ChatModel.GPT_4_O_MINI_AUDIO_PREVIEW)
                          .outputModalities(List.of("text", "audio"))
                          .outputAudio(new OpenAiApi.ChatCompletionRequest.AudioParameters(
                              OpenAiApi.ChatCompletionRequest.AudioParameters.Voice.ALLOY,
                              OpenAiApi.ChatCompletionRequest.AudioParameters.AudioResponseFormat.WAV));
        } else {
            optionsBuilder.model("o4-mini-2025-04-16");
        }

        return chatClient
                .prompt(chatResponse.get("prompt"))
                .system(chatResponse.get("systemMessage"))
                .user(chatResponse.get("userPrompt"))
                .options(optionsBuilder.build())
                .call()
                .chatResponse();
    }

    private Map<String, String> promptsMessage(ChatMessage question) {
        String promptContent = "Responde la pregunta correctamente teniendo en cuenta que pudo haber escrito incorrectamente, trata de interpretar lo que quiere decir";
        String userPrompt = "Responde a esta pregunta usando solo las columnas existentes en las tablas mencionadas: " + question.getQuestion();
        String systemMessage = """
                Eres un asistente que responde preguntas sobre una base de datos con las siguientes tablas:
                - employee (id, full_name, email, phone, available_vacation_days, status_id, role_id)
                - role (id, name)
                - calendar_event (id, title, description, start_date, end_date, event_type)
                - calendar_event_employees (calendar_event_id, employee_id) - tabla intermedia para relacionar eventos con empleados
                - status (id, name)
                - assist (employee_id, date, entry_time, exit_time, worked_hours, incidents, reason)

                Reglas de negocio:
                - La columna 'incidents' en assist puede ser: ASISTENCIA (asistió a tiempo), RETARDO (asistió con retraso), FALTA (no asistió).
                - RETARDO también cuenta como ASISTENCIA en conteos de asistencias.
                - Usa COUNT() y GROUP BY para estadísticas.
                - Para nombres con errores ortográficos (ej. 'Ana Martines' en lugar de 'Ana Martínez'), busca el nombre más similar usando ILIKE en PostgreSQL (ej. WHERE full_name ILIKE '%Ana Martinez%').
                - Para consultar eventos y empleados relacionados, usa JOINs con la tabla calendar_event_employees.

                Instrucciones:
                1. Analiza la pregunta para responder con coherencia y de la mejor forma posible.
                2. Genera una consulta SQL para validar los datos antes de responder.
                3. Usa la mejor consulta para evitar respuestas incorrectas.
                4. Responde en lenguaje natural, siendo claro y conciso sin agregar información que no se pidió.
                """;

        return Map.of(
            "prompt", promptContent,
            "userPrompt", userPrompt,
            "systemMessage", systemMessage
        );
    }

    public String audioToText(MultipartFile audioFile) {
        try {
            // Convertir el MultipartFile a ByteArrayResource
            ByteArrayResource audioResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };

            OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                    .language("es")
                    .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                    .temperature(0.2f)
                    .build();

            AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, options);
            AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt);
            String transcription = response.getResult().getOutput();
            return Normalizer.normalize(transcription, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        } catch (Exception e) {
            log.error("ChatbotService: Error processing audio file: {}", e.getMessage());
            throw new RuntimeException("Error procesando el archivo de audio", e);
        }
    }
}
