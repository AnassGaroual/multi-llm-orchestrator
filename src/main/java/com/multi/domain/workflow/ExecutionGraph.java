/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.NodeId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;

/**
 * Execution graph (DAG)
 *
 * <p>Represents workflow as directed acyclic graph Thread-safe: Immutable
 */
@DomainValueObject(name = "ExecutionGraph")
public record ExecutionGraph(
    @NonNull Map<NodeId, List<NodeId>> adjacencyList, @NonNull NodeId entryNode) {

  public ExecutionGraph {
    adjacencyList =
        Map.copyOf(
            adjacencyList.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> List.copyOf(e.getValue()))));
  }

  @FactoryMethod(description = "Create graph from edges")
  public static ExecutionGraph of(Map<NodeId, List<NodeId>> edges, NodeId entryNode) {
    return new ExecutionGraph(edges, entryNode);
  }

  /** Get all nodes in graph */
  public Set<NodeId> getAllNodes() {
    return Set.copyOf(adjacencyList.keySet());
  }

  /** Get root nodes (no incoming edges) */
  public Set<NodeId> getRootNodes() {
    var allNodes = getAllNodes();
    var nodesWithIncoming =
        adjacencyList.values().stream().flatMap(List::stream).collect(Collectors.toSet());

    return allNodes.stream()
        .filter(node -> !nodesWithIncoming.contains(node))
        .collect(Collectors.toSet());
  }

  /** Get leaf nodes (no outgoing edges) */
  public Set<NodeId> getLeafNodes() {
    return adjacencyList.entrySet().stream()
        .filter(e -> e.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  /** Get dependencies (incoming edges) */
  public Set<NodeId> getDependencies(NodeId nodeId) {
    return adjacencyList.entrySet().stream()
        .filter(e -> e.getValue().contains(nodeId))
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  /** Get successors (outgoing edges) */
  public List<NodeId> getSuccessors(NodeId nodeId) {
    return adjacencyList.getOrDefault(nodeId, List.of());
  }
}
