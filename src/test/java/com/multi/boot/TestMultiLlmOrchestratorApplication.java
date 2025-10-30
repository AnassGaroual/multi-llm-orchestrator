/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.boot;

import org.springframework.boot.SpringApplication;

public class TestMultiLlmOrchestratorApplication {

  public static void main(String[] args) {
    SpringApplication.from(MultiLlmOrchestratorApplication::main)
        .with(TestContainersConfiguration.class)
        .run(args);
  }
}
