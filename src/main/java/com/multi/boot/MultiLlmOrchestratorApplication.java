/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"m.b", "com.multi"})
public class MultiLlmOrchestratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(MultiLlmOrchestratorApplication.class, args);
  }
}
