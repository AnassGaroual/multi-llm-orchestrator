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

@DisplayName("ReduceNode")
class ReduceNodeTest {

  @Test
  void should_create_reduce_node() {
    var inputs = List.of(NodeId.of("in1"), NodeId.of("in2"));

    var node =
        ReduceNode.builder()
            .id(NodeId.of("reduce"))
            .role("Reducer")
            .strategy("concatenate")
            .provider("openai:gpt-5o")
            .systemPrompt("Merge inputs")
            .inputs(inputs)
            .constraints(NodeConstraints.defaults())
            .nextNodes(List.of())
            .build();

    assertThat(node.getInputs()).hasSize(2);
  }

  @Test
  void should_require_at_least_one_input() {
    assertThatThrownBy(
            () ->
                ReduceNode.builder()
                    .id(NodeId.of("reduce"))
                    .role("Reducer")
                    .strategy("concatenate")
                    .provider("openai:gpt-5o")
                    .systemPrompt("test")
                    .inputs(List.of())
                    .constraints(NodeConstraints.defaults())
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("at least one input");
  }

  @Test
  void should_validate_all_inputs_exist() {
    var reduce =
        ReduceNode.builder()
            .id(NodeId.of("reduce"))
            .role("Reducer")
            .strategy("concatenate")
            .provider("openai:gpt-5o")
            .systemPrompt("test")
            .inputs(List.of(NodeId.of("in1"), NodeId.of("ghost")))
            .constraints(NodeConstraints.defaults())
            .nextNodes(List.of())
            .build();

    var allIds = Set.of(NodeId.of("reduce"), NodeId.of("in1"));

    assertThatThrownBy(() -> reduce.validateReferences(allIds))
        .hasMessageContaining("ghost")
        .hasMessageContaining("input");
  }
}
