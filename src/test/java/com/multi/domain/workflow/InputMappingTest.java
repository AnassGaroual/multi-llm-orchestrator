/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InputMapping Value Object")
class InputMappingTest {

  @Test
  void should_map_direct_values() {
    var mapping = InputMapping.of(Map.of("query", "{{user.query}}"));

    Map<String, Object> context = Map.of("user", Map.of("query", "test question"));

    var result = mapping.apply(context);

    assertThat(result).containsEntry("query", "test question");
  }

  @Test
  void should_map_multiple_fields() {
    var mapping =
        InputMapping.of(
            Map.of(
                "query", "{{user.query}}",
                "language", "{{user.language}}"));

    Map<String, Object> context =
        Map.of(
            "user",
            Map.of(
                "query", "test",
                "language", "en"));

    var result = mapping.apply(context);

    assertThat(result).containsEntry("query", "test").containsEntry("language", "en");
  }

  @Test
  void should_handle_missing_placeholders() {
    var mapping = InputMapping.of(Map.of("query", "{{user.missing}}"));

    Map<String, Object> context = Map.of("user", Map.of("query", "test"));

    var result = mapping.apply(context);

    assertThat(result).containsEntry("query", "{{user.missing}}");
  }

  @Test
  void should_create_passthrough_mapping() {
    var mapping = InputMapping.passthrough();

    Map<String, Object> context = Map.of("key", "value");

    var result = mapping.apply(context);

    assertThat(result).containsEntry("key", "value");
  }
}
