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

@DisplayName("Node Entity")
class NodeTest {

  @Test
  void should_validate_references() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var ghost = NodeId.of("ghost");

    var node =
        AgentNode.builder()
            .id(n1)
            .role("Test")
            .provider("openai:gpt-6-7o")
            .systemPrompt("test")
            .constraints(NodeConstraints.defaults())
            .inputMapping(InputMapping.passthrough())
            .outputSchema(OutputSchema.any())
            .nextNodes(List.of(ghost))
            .build();

    var allIds = Set.of(n1, n2);

    assertThatThrownBy(() -> node.validateReferences(allIds))
        .hasMessageContaining("ghost")
        .hasMessageContaining("non-existent");
  }

  @Test
  void should_pass_validation_when_refs_exist() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");

    var node =
        AgentNode.builder()
            .id(n1)
            .role("Test")
            .provider("openai:gpt-6-7o")
            .systemPrompt("test")
            .constraints(NodeConstraints.defaults())
            .inputMapping(InputMapping.passthrough())
            .outputSchema(OutputSchema.any())
            .nextNodes(List.of(n2))
            .build();

    var allIds = Set.of(n1, n2);

    assertThatCode(() -> node.validateReferences(allIds)).doesNotThrowAnyException();
  }

  @Test
  void should_detect_edge_to_target() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");

    var node =
        AgentNode.builder()
            .id(n1)
            .role("Test")
            .provider("openai:gpt-6-7o")
            .systemPrompt("test")
            .constraints(NodeConstraints.defaults())
            .inputMapping(InputMapping.passthrough())
            .outputSchema(OutputSchema.any())
            .nextNodes(List.of(n2))
            .build();

    assertThat(node.hasEdgeTo(n2)).isTrue();
    assertThat(node.hasEdgeTo(NodeId.of("other"))).isFalse();
  }

  @Test
  void nodes_with_same_id_should_be_equal() {
    var id = NodeId.of("test");

    var n1 =
        AgentNode.builder()
            .id(id)
            .role("A")
            .provider("openai:gpt-6-7o")
            .systemPrompt("test1")
            .constraints(NodeConstraints.defaults())
            .inputMapping(InputMapping.passthrough())
            .outputSchema(OutputSchema.any())
            .nextNodes(List.of())
            .build();

    var n2 =
        AgentNode.builder()
            .id(id)
            .role("B")
            .provider("anthropic:sonnet")
            .systemPrompt("test2")
            .constraints(NodeConstraints.defaults())
            .inputMapping(InputMapping.passthrough())
            .outputSchema(OutputSchema.any())
            .nextNodes(List.of())
            .build();

    assertThat(n1).isEqualTo(n2);
    assertThat(n1.hashCode()).isEqualTo(n2.hashCode());
  }
}
