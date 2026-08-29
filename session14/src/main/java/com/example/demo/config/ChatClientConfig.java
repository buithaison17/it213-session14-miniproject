package com.example.demo.config;

import com.example.demo.mcp.LogisticsMcpTool;
import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().chatMemoryRepository(new InMemoryChatMemoryRepository()).maxMessages(20).build();
    }

    @Bean
    public ChatClient client(ChatClient.Builder builder, ChatMemory chatMemory, McpSyncClient mcpSyncClient, LogisticsMcpTool logisticsMcpTool) {
        return builder
                .defaultTools(McpToolUtils.getToolCallbacksFromSyncClients(mcpSyncClient), logisticsMcpTool)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
