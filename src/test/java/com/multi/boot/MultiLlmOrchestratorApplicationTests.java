/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.boot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestContainersConfiguration.class)
@SpringBootTest(
    classes = MultiLlmOrchestratorApplicationTests.class,
    properties = {
      "spring.mvc.throw-exception-if-no-handler-found=true",
      "spring.web.resources.add-mappings=false",
      "app.http.correlation-header=X-Correlation-Id",

      // disable JDBC
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
          + "org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration,"
    })
class MultiLlmOrchestratorApplicationTests {

  @Test
  void contextLoads() {}
}
