package com.example.demo.config;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;

@Configuration
public class McpClientConfig {
    @Value("${spring.ai.mcp.server.postgres.connection-string}")
    private String connectionString;

    @Bean
    public McpSyncClient mcpSyncClient(JsonMapper jsonMapper) {
        ServerParameters serverParameters = ServerParameters.builder("npx.cmd")
                .args(List.of(
                        "-y",
                        "@modelcontextprotocol/server-postgres",
                        connectionString
                ))
                .build();
        StdioClientTransport transport = new StdioClientTransport(serverParameters, new JacksonMcpJsonMapper(jsonMapper));
        McpSyncClient client = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(20))
                .build();
        client.initialize();
        return client;
    }
}