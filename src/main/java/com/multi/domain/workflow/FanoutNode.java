/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.*;
import java.util.List;
import java.util.Set;
import lombok.*;

/**
 * Fanout node: Parallel execution
 *
 * <p>Launches multiple branches concurrently Uses Virtual Threads for massive parallelism
 */
@Value
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class FanoutNode extends Node {

  List<NodeId> branches;

  private FanoutNode(NodeId id, String role, List<NodeId> branches, List<NodeId> nextNodes) {
    super(id, role, nextNodes);

    if (branches == null || branches.isEmpty()) {
      throw new ValidationException("Fanout must have at least one branch");
    }

    this.branches = List.copyOf(branches);
  }

  @FactoryMethod
  public static FanoutNodeBuilder builder() {
    return new FanoutNodeBuilder();
  }

  public static class FanoutNodeBuilder {
    private NodeId id;
    private String role;
    private List<NodeId> branches;
    private List<NodeId> nextNodes = List.of();

    public FanoutNodeBuilder id(NodeId id) {
      this.id = id;
      return this;
    }

    public FanoutNodeBuilder role(String role) {
      this.role = role;
      return this;
    }

    public FanoutNodeBuilder branches(List<NodeId> branches) {
      this.branches = branches;
      return this;
    }

    public FanoutNodeBuilder nextNodes(List<NodeId> nextNodes) {
      this.nextNodes = nextNodes;
      return this;
    }

    public FanoutNode build() {
      return new FanoutNode(id, role, branches, nextNodes);
    }
  }

  @Override
  public void validateReferences(Set<NodeId> allNodeIds) {
    super.validateReferences(allNodeIds);

    branches.forEach(
        branch -> {
          if (!allNodeIds.contains(branch)) {
            throw new ValidationException(
                "Fanout %s references non-existent branch: %s"
                    .formatted(getId().value(), branch.value()));
          }
        });
  }
}
