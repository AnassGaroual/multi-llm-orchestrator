/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.DomainEntity;
import com.multi.domain.annotation.InvariantRule;
import com.multi.domain.shared.*;
import java.util.List;
import java.util.Set;
import lombok.*;

/**
 * Node entity (abstract base)
 *
 * <p>Entity: Has identity (NodeId) Equality based on ID only
 *
 * <p>SOLID: - SRP: Node definition only - OCP: Extensible via sealed - LSP: All nodes substitutable
 *
 * <p>Thread-safe: Immutable (Lombok @Value on subclasses)
 */
@DomainEntity(name = "Node")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract sealed class Node
    permits AgentNode, FanoutNode, ReduceNode, VetoNode, VoteNode, LoopNode {

  @EqualsAndHashCode.Include private final NodeId id;
  private final String role;
  private final List<NodeId> nextNodes;

  protected Node(NodeId id, String role, List<NodeId> nextNodes) {
    this.id = id;
    this.role = role;
    this.nextNodes = nextNodes != null ? List.copyOf(nextNodes) : List.of();
  }

  @InvariantRule("All next node references must exist in workflow")
  public void validateReferences(Set<NodeId> allNodeIds) {
    nextNodes.forEach(
        next -> {
          if (!allNodeIds.contains(next)) {
            throw new ValidationException(
                "Node %s references non-existent node: %s".formatted(id.value(), next.value()));
          }
        });
  }

  public boolean hasEdgeTo(NodeId target) {
    return nextNodes.contains(target);
  }
}
