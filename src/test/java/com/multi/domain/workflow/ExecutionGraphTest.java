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

@DisplayName("ExecutionGraph Value Object")
class ExecutionGraphTest {

  @Test
  void should_create_simple_graph() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");

    var edges =
        Map.of(
            n1, List.of(n2, n3),
            n2, List.<NodeId>of(),
            n3, List.<NodeId>of());

    var graph = ExecutionGraph.of(edges, n1);

    assertThat(graph.entryNode()).isEqualTo(n1);
    assertThat(graph.getAllNodes()).containsExactlyInAnyOrder(n1, n2, n3);
  }

  @Test
  void should_identify_root_nodes() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");

    var edges =
        Map.of(
            n1, List.of(n3),
            n2, List.of(n3),
            n3, List.<NodeId>of());

    var graph = ExecutionGraph.of(edges, n1);

    assertThat(graph.getRootNodes()).containsExactlyInAnyOrder(n1, n2);
  }

  @Test
  void should_identify_leaf_nodes() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");

    var edges =
        Map.of(
            n1, List.of(n2, n3),
            n2, List.<NodeId>of(),
            n3, List.<NodeId>of());

    var graph = ExecutionGraph.of(edges, n1);

    assertThat(graph.getLeafNodes()).containsExactlyInAnyOrder(n2, n3);
  }

  @Test
  void should_get_dependencies() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");
    var n4 = NodeId.of("n4");

    var edges =
        Map.of(
            n1, List.of(n3),
            n2, List.of(n3),
            n3, List.of(n4),
            n4, List.<NodeId>of());

    var graph = ExecutionGraph.of(edges, n1);

    assertThat(graph.getDependencies(n3)).containsExactlyInAnyOrder(n1, n2);
    assertThat(graph.getDependencies(n4)).containsExactly(n3);
    assertThat(graph.getDependencies(n1)).isEmpty();
  }

  @Test
  void should_get_successors() {
    var n1 = NodeId.of("n1");
    var n2 = NodeId.of("n2");
    var n3 = NodeId.of("n3");

    var edges =
        Map.of(
            n1, List.of(n2, n3),
            n2, List.<NodeId>of(),
            n3, List.<NodeId>of());

    var graph = ExecutionGraph.of(edges, n1);

    assertThat(graph.getSuccessors(n1)).containsExactlyInAnyOrder(n2, n3);
    assertThat(graph.getSuccessors(n2)).isEmpty();
  }
}
