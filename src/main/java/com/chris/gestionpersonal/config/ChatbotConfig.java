package com.chris.gestionpersonal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ChatbotConfig {

    @Bean
    ChatClient chatClient(ChatModel chatModel, @Autowired(required = false) SyncMcpToolCallbackProvider toolCallbackProvider) {
        if (toolCallbackProvider != null) {
            return ChatClient
                    .builder(chatModel)
                    .defaultTools(toolCallbackProvider.getToolCallbacks())
                    .build();
        } else {
            return ChatClient.builder(chatModel).build();
        }
    }
}