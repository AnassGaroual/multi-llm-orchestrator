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

@DisplayName("VetoNode")
class VetoNodeTest {

  @Test
  void should_create_veto_node() {
    var rules = Map.of("minQualityScore", 15.0, "blockedPatterns", List.of("spam", "scam"));

    var node =
        VetoNode.builder()
            .id(NodeId.of("veto"))
            .role("Quality Gate")
            .rules(rules)
            .onFailNode(NodeId.of("retry"))
            .nextNodes(List.of())
            .build();

    assertThat(node.getRules()).containsKey("minQualityScore");
  }

  @Test
  void should_require_at_least_one_rule() {
    assertThatThrownBy(
            () ->
                VetoNode.builder()
                    .id(NodeId.of("veto"))
                    .role("Gate")
                    .rules(Map.of())
                    .onFailNode(NodeId.of("retry"))
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("at least one rule");
  }

  @Test
  void should_validate_onFail_node_exists() {
    var veto =
        VetoNode.builder()
            .id(NodeId.of("veto"))
            .role("Gate")
            .rules(Map.of("minScore", 10.0))
            .onFailNode(NodeId.of("ghost"))
            .nextNodes(List.of())
            .build();

    var allIds = Set.of(NodeId.of("veto"));

    assertThatThrownBy(() -> veto.validateReferences(allIds))
        .hasMessageContaining("ghost")
        .hasMessageContaining("onFail");
  }
}
