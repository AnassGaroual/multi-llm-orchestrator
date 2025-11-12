/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.*;
import com.multi.domain.event.WorkflowPublished;
import com.multi.domain.shared.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Workflow Aggregate Root
 *
 * <p>Bounded Context: Workflow Management
 *
 * <p>Invariants: - Must be a DAG (no cycles) - Entry node must exist - All references must be valid
 * - Immutable after publication
 *
 * <p>Thread-safe: Immutable builder pattern Event-driven: Emits WorkflowPublished
 */
@Slf4j
@DomainAggregate(name = "Workflow", boundedContext = "WorkflowManagement")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Workflow {

  @EqualsAndHashCode.Include private final WorkflowId id;

  private final String name;
  private final String schemaVersion;
  private final NodeId entryNode;
  private final Map<NodeId, Node> nodes;
  private final WorkflowStatus status;
  private final long aggregateVersion;

  private Workflow(
      WorkflowId id,
      String name,
      String schemaVersion,
      NodeId entryNode,
      Map<NodeId, Node> nodes,
      WorkflowStatus status,
      long aggregateVersion) {
    this.id = id;
    this.name = name;
    this.schemaVersion = schemaVersion;
    this.entryNode = entryNode;
    this.nodes = Map.copyOf(nodes);
    this.status = status;
    this.aggregateVersion = aggregateVersion;
  }

  @FactoryMethod(description = "Create new workflow")
  public static Workflow define(String tenantId, String name) {
    log.debug("Defining new workflow: {}", name);
    return new Workflow(
        WorkflowId.generate(tenantId), name, "1.0", null, Map.of(), WorkflowStatus.DRAFT, 0);
  }

  /** Add node (returns new instance) */
  @InvariantRule("Node IDs must be unique within workflow")
  public Workflow withNode(Node node) {
    ensureDraft();

    var newNodes = new HashMap<>(this.nodes);
    if (newNodes.putIfAbsent(node.getId(), node) != null) {
      throw new ValidationException("Node already exists: " + node.getId().value());
    }

    log.debug("Added node {} to workflow {}", node.getId(), id);
    return new Workflow(id, name, schemaVersion, entryNode, newNodes, status, aggregateVersion);
  }

  /** Set entry point (returns new instance) */
  @InvariantRule("Entry node must exist in workflow")
  public Workflow withEntryNode(NodeId entryNode) {
    ensureDraft();

    if (!nodes.containsKey(entryNode)) {
      throw new ValidationException("Entry node does not exist: " + entryNode.value());
    }

    log.debug("Set entry node {} for workflow {}", entryNode, id);
    return new Workflow(id, name, schemaVersion, entryNode, nodes, status, aggregateVersion);
  }

  /** Publish workflow (validates + emits event) */
  @InvariantRule("Workflow must be valid before publication")
  public PublishResult publish(String correlationId) {
    validate();

    var publishedWorkflow =
        new Workflow(
            id,
            name,
            schemaVersion,
            entryNode,
            nodes,
            WorkflowStatus.PUBLISHED,
            aggregateVersion + 1);

    var event = WorkflowPublished.create(id, correlationId, publishedWorkflow.aggregateVersion);

    log.info("Workflow {} published (version {})", id, publishedWorkflow.aggregateVersion);
    return new PublishResult(publishedWorkflow, event);
  }

  /** Validate all invariants */
  @InvariantRule("Workflow must satisfy all consistency rules")
  public void validate() {
    // Check nodes first
    if (nodes.isEmpty()) {
      throw new ValidationException("Workflow has no nodes");
    }

    // Then check entry node
    if (entryNode == null) {
      throw new ValidationException("Entry node not defined");
    }

    nodes.values().forEach(node -> node.validateReferences(nodes.keySet()));

    detectCycles();

    log.debug("Workflow {} validated successfully", id);
  }

  /** Detect cycles using DFS starting from entry node */
  @InvariantRule("Workflow must be a DAG (no cycles)")
  private void detectCycles() {
    var visited = new HashSet<NodeId>();
    var recStack = new HashSet<NodeId>();

    // Start from entry node for deterministic behavior
    if (hasCycleDFS(entryNode, visited, recStack)) {
      throw new CycleDetectedException(entryNode.value());
    }

    // Also check unreachable nodes
    for (NodeId nodeId : nodes.keySet()) {
      if (!visited.contains(nodeId)) {
        if (hasCycleDFS(nodeId, visited, recStack)) {
          throw new CycleDetectedException(nodeId.value());
        }
      }
    }
  }

  private boolean hasCycleDFS(NodeId current, Set<NodeId> visited, Set<NodeId> recStack) {
    // Already in recursion stack = cycle found
    if (recStack.contains(current)) {
      return true;
    }

    // Already visited and cleared = no cycle
    if (visited.contains(current)) {
      return false;
    }

    // Mark as visiting
    visited.add(current);
    recStack.add(current);

    // Visit all neighbors
    Node node = nodes.get(current);
    if (node != null) {
      for (NodeId next : node.getNextNodes()) {
        if (hasCycleDFS(next, visited, recStack)) {
          return true;
        }
      }
    }

    // Remove from recursion stack (backtrack)
    recStack.remove(current);
    return false;
  }

  private void ensureDraft() {
    if (status != WorkflowStatus.DRAFT) {
      throw new IllegalStateException("Cannot modify published workflow");
    }
  }

  public record PublishResult(Workflow workflow, WorkflowPublished event) {}
}
