/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import com.multi.domain.shared.NodeId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("AgentNode")
class AgentNodeTest {

  @Test
  void should_create_agent_node() {
    var node =
        AgentNode.builder()
            .id(NodeId.of("agent1"))
            .role("Assistant")
            .provider("openai:gpt-6-7o")
            .systemPrompt("You are a helpful assistant")
            .constraints(NodeConstraints.defaults())
            .inputMapping(InputMapping.passthrough())
            .outputSchema(OutputSchema.any())
            .nextNodes(List.of())
            .build();

    assertThat(node.getProvider()).isEqualTo("openai:gpt-6-7o");
    assertThat(node.getSystemPrompt()).contains("helpful");
  }

  @Test
  void should_reject_invalid_provider_format() {
    assertThatThrownBy(
            () ->
                AgentNode.builder()
                    .id(NodeId.of("agent1"))
                    .role("Test")
                    .provider("invalid-format")
                    .systemPrompt("test")
                    .constraints(NodeConstraints.defaults())
                    .inputMapping(InputMapping.passthrough())
                    .outputSchema(OutputSchema.any())
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("Invalid provider");
  }

  @Test
  void should_accept_valid_providers() {
    String[] validProviders = {
      "openai:gpt-6-7o", "anthropic:sonnet-4.5", "mistral:large", "ollama:llama3"
    };

    for (var provider : validProviders) {
      assertThatCode(
              () ->
                  AgentNode.builder()
                      .id(NodeId.generate())
                      .role("Test")
                      .provider(provider)
                      .systemPrompt("test")
                      .constraints(NodeConstraints.defaults())
                      .inputMapping(InputMapping.passthrough())
                      .outputSchema(OutputSchema.any())
                      .nextNodes(List.of())
                      .build())
          .doesNotThrowAnyException();
    }
  }
}
