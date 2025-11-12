/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.service;

import static org.assertj.core.api.Assertions.*;

import com.multi.domain.shared.NodeId;
import com.multi.domain.workflow.*;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CycleDetectionService")
class CycleDetectionServiceTest {

  private final CycleDetectionService service = new CycleDetectionService();

  @Test
  void should_detect_simple_cycle() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");

    Map<NodeId, Node> nodes =
        Map.of(
            n1, createNodeWithNext(n1, List.of(n2)),
            n2, createNodeWithNext(n2, List.of(n1)));

    assertThat(service.hasCycle(nodes)).isTrue();
  }

  @Test
  void should_detect_self_loop() {
    var n1 = NodeId.of("n1");

    Map<NodeId, Node> nodes = Map.of(n1, createNodeWithNext(n1, List.of(n1)));

    assertThat(service.hasCycle(nodes)).isTrue();
  }

  @Test
  void should_accept_valid_dag() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");

    Map<NodeId, Node> nodes =
        Map.of(
            n1, createNodeWithNext(n1, List.of(n2, n3)),
            n2, createNodeWithNext(n2, List.of()),
            n3, createNodeWithNext(n3, List.of()));

    assertThat(service.hasCycle(nodes)).isFalse();
  }

  @Test
  void should_detect_long_cycle() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");
    var n4 = NodeId.of("n4");

    Map<NodeId, Node> nodes =
        Map.of(
            n1, createNodeWithNext(n1, List.of(n2)),
            n2, createNodeWithNext(n2, List.of(n3)),
            n3, createNodeWithNext(n3, List.of(n4)),
            n4, createNodeWithNext(n4, List.of(n2)));

    assertThat(service.hasCycle(nodes)).isTrue();
  }

  private AgentNode createNodeWithNext(NodeId id, List<NodeId> next) {
    return AgentNode.builder()
        .id(id)
        .role("Test")
        .provider("openai:gpt-6-7o")
        .systemPrompt("test")
        .constraints(NodeConstraints.defaults())
        .inputMapping(InputMapping.passthrough())
        .outputSchema(OutputSchema.any())
        .nextNodes(next)
        .build();
  }
}
