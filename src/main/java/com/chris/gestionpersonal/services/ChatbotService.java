package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.models.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatbotService {

    private final ChatClient chatClient;

    public ChatMessage chat(ChatMessage question) {
        ChatMessage chatResponse = new ChatMessage();
        log.info("ChatbotService: Sending question to AI: {}", question.getQuestion());
        chatResponse.setQuestion(retryPromptMessage(question));
        return chatResponse;
    }

    private String retryPromptMessage(ChatMessage question) {
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            String currentPrompt = "Responde a esta pregunta usando solo las columnas existentes en las tablas mencionadas: " + question.getQuestion();
            String systemMessage =
                    "La base de datos tiene tablas: employee (id, full_name, email, role_id, status_id), " +
                            "role (id, name), status (id, name), assist (employee_id, date, entry_time, exit_time, " +
                            "worked_hours, incidents, reason). " +
                            "Las columnas incidents de asistencias son: ASISTENCIA (asistió a tiempo), RETARDO (asistió con retraso) y FALTA (no asistió). pero recuerdo que RETARDO tambien cuenta como ASISTENCIA pero llego tarde" +
                            "Para obtener conteos o estadísticas, debes realizar cálculos explícitos como COUNT() o agrupar con GROUP BY. " +
                            "Usa esta consulta como referencia, pero modifícala según sea necesario para responder correctamente: " +
                            "SELECT e.id, e.full_name, e.email, e.phone, r.name as rol, s.name as estado, " +
                            "a.date, a.entry_time, a.exit_time, a.worked_hours, a.incidents, a.reason " +
                            "FROM employee e " +
                            "LEFT JOIN role r ON e.role_id = r.id " +
                            "LEFT JOIN status s ON e.status_id = s.id " +
                            "LEFT JOIN assist a ON e.id = a.employee_id " +
                            "WHERE a.incidents = 'FALTA' AND EXTRACT(MONTH FROM a.date) = 4 AND EXTRACT(YEAR FROM a.date) = 2025 " +
                            "ORDER BY e.full_name; " +
                            "Responde con lenguaje natural, siendo claro y conciso y no agregues informacion que el usuario no pidio. No intentes usar columnas que no existen.";

            if (retryCount > 0) {
                currentPrompt = retryCount == 1 ? " SELECT \\n    e.id as id_empleado,\\n    e.full_name as nombre_completo,\\n    e.email as correo,\\n    e.phone as telefono,\\n    r.name as rol,\\n    s.name as estado,\\n    a.date as fecha_asistencia,\\n    a.entry_time as hora_entrada,\\n    a.exit_time as hora_salida,\\n    a.worked_hours as horas_trabajadas,\\n    a.incidents as incidencia,\\n    a.reason as razon_incidencia\\nFROM employee e\\nLEFT JOIN role r ON e.role_id = r.id\\nLEFT JOIN status s ON e.status_id = s.id\\nLEFT JOIN assist a ON e.id = a.employee_id\\nORDER BY e.full_name, a.date DESC; " + question.getQuestion() :
                    " SELECT * FROM assist;: SELECT * FROM employee; SELECT * FROM role; SELECT * from status;  " + question.getQuestion();
                log.info("Retry attempt {}: Using modified prompt: {}", retryCount, currentPrompt);
            }
            try {
                String response = chatClient
                        .prompt()
                        .system(systemMessage)
                        .user(currentPrompt)
                        .call()
                        .content();
                log.info("ChatbotService: Received response from AI: {}", response);

                return response;

            } catch (Exception e) {
                retryCount++;
                log.warn("Error calling chat (attempt {}/{}): {}", retryCount, maxRetries, e.getMessage());

                if (retryCount >= maxRetries) {
                    throw new RuntimeException("Error communicating with the AI service after multiple attempts", e);
                }
            }
        }
        return " No se pudo obtener una respuesta válida del chatbot después de varios intentos.";
    }
}