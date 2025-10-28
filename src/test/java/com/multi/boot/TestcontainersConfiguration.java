package com.multi.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "tc", name = "enabled", havingValue = "true")
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    OllamaContainer ollamaContainer() {
        return new OllamaContainer(DockerImageName.parse("ollama/ollama:latest"));
    }

}
