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

@DisplayName("FanoutNode")
class FanoutNodeTest {

  @Test
  void should_create_fanout_node() {
    var branches = List.of(NodeId.of("b1"), NodeId.of("b2"));

    var node =
        FanoutNode.builder()
            .id(NodeId.of("fanout"))
            .role("Fanout")
            .branches(branches)
            .nextNodes(List.of())
            .build();

    assertThat(node.getBranches()).hasSize(2);
  }

  @Test
  void should_require_at_least_one_branch() {
    assertThatThrownBy(
            () ->
                FanoutNode.builder()
                    .id(NodeId.of("fanout"))
                    .role("Fanout")
                    .branches(List.of())
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("at least one branch");
  }

  @Test
  void should_validate_all_branches_exist() {
    var fanout =
        FanoutNode.builder()
            .id(NodeId.of("fanout"))
            .role("Fanout")
            .branches(List.of(NodeId.of("b1"), NodeId.of("ghost")))
            .nextNodes(List.of())
            .build();

    var allIds = Set.of(NodeId.of("fanout"), NodeId.of("b1"));

    assertThatThrownBy(() -> fanout.validateReferences(allIds))
        .hasMessageContaining("ghost")
        .hasMessageContaining("branch");
  }
}
