/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import com.multi.domain.shared.NodeId;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoopNode")
class LoopNodeTest {

  @Test
  void should_create_loop_node() {
    List<Node> body =
        List.of(
            AgentNode.builder()
                .id(NodeId.of("body1"))
                .role("Worker")
                .provider("openai:gpt-5o")
                .systemPrompt("test")
                .constraints(NodeConstraints.defaults())
                .inputMapping(InputMapping.passthrough())
                .outputSchema(OutputSchema.any())
                .nextNodes(List.of())
                .build());

    var node =
        LoopNode.builder()
            .id(NodeId.of("loop"))
            .role("Iterative")
            .maxIterations(5)
            .body(body)
            .nextNodes(List.of())
            .build();

    assertThat(node.getMaxIterations()).isEqualTo(5);
    assertThat(node.getBody()).hasSize(1);
  }

  @Test
  void should_require_at_least_one_body_node() {
    assertThatThrownBy(
            () ->
                LoopNode.builder()
                    .id(NodeId.of("loop"))
                    .role("Iterative")
                    .maxIterations(5)
                    .body(List.of())
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("at least one body node");
  }

  @Test
  void should_reject_negative_max_iterations() {
    assertThatThrownBy(
            () ->
                LoopNode.builder()
                    .id(NodeId.of("loop"))
                    .role("Iterative")
                    .maxIterations(-1)
                    .body(
                        List.of(
                            AgentNode.builder()
                                .id(NodeId.of("body"))
                                .role("Worker")
                                .provider("openai:gpt-5o")
                                .systemPrompt("test")
                                .constraints(NodeConstraints.defaults())
                                .inputMapping(InputMapping.passthrough())
                                .outputSchema(OutputSchema.any())
                                .nextNodes(List.of())
                                .build()))
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("Max iterations must be >= 1");
  }

  @Test
  void should_default_to_one_iteration() {
    var node =
        LoopNode.builder()
            .id(NodeId.of("loop"))
            .role("Iterative")
            .body(
                List.of(
                    AgentNode.builder()
                        .id(NodeId.of("body"))
                        .role("Worker")
                        .provider("openai:gpt-5o")
                        .systemPrompt("test")
                        .constraints(NodeConstraints.defaults())
                        .inputMapping(InputMapping.passthrough())
                        .outputSchema(OutputSchema.any())
                        .nextNodes(List.of())
                        .build()))
            .nextNodes(List.of())
            .build();

    assertThat(node.getMaxIterations()).isEqualTo(1);
  }
}
