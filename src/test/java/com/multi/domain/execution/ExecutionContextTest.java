/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.execution;

import static org.assertj.core.api.Assertions.*;

import com.multi.domain.shared.NodeId;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExecutionContext Value Object")
class ExecutionContextTest {

  @Test
  void should_create_initial_context() {
    Map<String, Object> userInputs = Map.of("goal", "test");

    var context = ExecutionContext.initial(userInputs);

    assertThat(context.getVariable("user.goal")).isEqualTo("test");
    assertThat(context.correlationId()).isNotBlank();
  }

  @Test
  void should_add_variable() {
    var context = ExecutionContext.initial(Map.of());

    var updated = context.withVariable("key", "value");

    assertThat(updated.getVariable("key")).isEqualTo("value");
  }

  @Test
  void should_be_immutable() {
    var context = ExecutionContext.initial(Map.of());

    var updated = context.withVariable("key", "value");

    assertThat(context.getVariable("key")).isNull();
    assertThat(updated.getVariable("key")).isEqualTo("value");
  }

  @Test
  void should_add_result() {
    var context = ExecutionContext.initial(Map.of());
    var nodeId = NodeId.of("node1");

    var updated = context.withResult(nodeId, Map.of("output", "test"));

    assertThat(updated.getResult(nodeId)).containsEntry("output", "test");
  }

  @Test
  void should_render_template() {
    var context = ExecutionContext.initial(Map.of("goal", "test question"));

    var rendered = context.renderTemplate("Goal: {{user.goal}}");

    assertThat(rendered).isEqualTo("Goal: test question");
  }

  @Test
  void should_render_nested_template() {
    var context = ExecutionContext.initial(Map.of());

    var configMap = new HashMap<String, Object>();
    configMap.put("model", "gpt-5o");

    var updated = context.withVariable("config", configMap);

    var rendered = updated.renderTemplate("Using {{config.model}}");

    assertThat(rendered).isEqualTo("Using gpt-5o");
  }

  @Test
  void should_handle_missing_placeholder() {
    var context = ExecutionContext.initial(Map.of());

    var rendered = context.renderTemplate("Value: {{missing.key}}");

    assertThat(rendered).isEqualTo("Value: {{missing.key}}");
  }
}
