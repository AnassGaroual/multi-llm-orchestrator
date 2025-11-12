/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.*;
import java.util.List;
import lombok.*;

/**
 * Loop node: Iterative execution
 *
 * <p>Repeats body nodes for maxIterations Useful for refinement workflows
 */
@Value
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class LoopNode extends Node {

  int maxIterations;
  List<Node> body;

  private LoopNode(
      NodeId id, String role, int maxIterations, List<Node> body, List<NodeId> nextNodes) {
    super(id, role, nextNodes);

    if (body == null || body.isEmpty()) {
      throw new ValidationException("Loop must have at least one body node");
    }

    if (maxIterations < 1) {
      throw new ValidationException("Max iterations must be >= 1");
    }

    this.maxIterations = maxIterations;
    this.body = List.copyOf(body);
  }

  @FactoryMethod
  public static LoopNodeBuilder builder() {
    return new LoopNodeBuilder();
  }

  public static class LoopNodeBuilder {
    private NodeId id;
    private String role;
    private int maxIterations = 1; // Default
    private List<Node> body;
    private List<NodeId> nextNodes = List.of();

    public LoopNodeBuilder id(NodeId id) {
      this.id = id;
      return this;
    }

    public LoopNodeBuilder role(String role) {
      this.role = role;
      return this;
    }

    public LoopNodeBuilder maxIterations(int maxIterations) {
      this.maxIterations = maxIterations;
      return this;
    }

    public LoopNodeBuilder body(List<Node> body) {
      this.body = body;
      return this;
    }

    public LoopNodeBuilder nextNodes(List<NodeId> nextNodes) {
      this.nextNodes = nextNodes;
      return this;
    }

    public LoopNode build() {
      return new LoopNode(id, role, maxIterations, body, nextNodes);
    }
  }
}
