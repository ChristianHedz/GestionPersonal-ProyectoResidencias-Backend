package com.chris.gestionpersonal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
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
        ChatClient.Builder builder = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(),
                        new PromptChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultUser(user ->
                        user.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 3));
        if (toolCallbackProvider != null) {
            builder
                .defaultTools(toolCallbackProvider.getToolCallbacks());
        }
        return builder.build();
    }
}