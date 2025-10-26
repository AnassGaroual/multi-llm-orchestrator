package com.multi.orchestrator;

import org.springframework.boot.SpringApplication;

public class TestMultiLlmOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.from(MultiLlmOrchestratorApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
