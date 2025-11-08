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

/** Reduce node: Merge multiple inputs */
@Value
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class ReduceNode extends Node {

  String strategy;
  String provider;
  String systemPrompt;
  List<NodeId> inputs;
  NodeConstraints constraints;

  private ReduceNode(
      NodeId id,
      String role,
      String strategy,
      String provider,
      String systemPrompt,
      List<NodeId> inputs,
      NodeConstraints constraints,
      List<NodeId> nextNodes) {
    super(id, role, nextNodes);

    if (inputs == null || inputs.isEmpty()) {
      throw new ValidationException("Reduce must have at least one input");
    }

    this.strategy = strategy;
    this.provider = provider;
    this.systemPrompt = systemPrompt;
    this.inputs = List.copyOf(inputs);
    this.constraints = constraints;
  }

  @FactoryMethod
  public static ReduceNodeBuilder builder() {
    return new ReduceNodeBuilder();
  }

  public static class ReduceNodeBuilder {
    private NodeId id;
    private String role;
    private String strategy;
    private String provider;
    private String systemPrompt;
    private List<NodeId> inputs;
    private NodeConstraints constraints;
    private List<NodeId> nextNodes = List.of();

    public ReduceNodeBuilder id(NodeId id) {
      this.id = id;
      return this;
    }

    public ReduceNodeBuilder role(String role) {
      this.role = role;
      return this;
    }

    public ReduceNodeBuilder strategy(String strategy) {
      this.strategy = strategy;
      return this;
    }

    public ReduceNodeBuilder provider(String provider) {
      this.provider = provider;
      return this;
    }

    public ReduceNodeBuilder systemPrompt(String systemPrompt) {
      this.systemPrompt = systemPrompt;
      return this;
    }

    public ReduceNodeBuilder inputs(List<NodeId> inputs) {
      this.inputs = inputs;
      return this;
    }

    public ReduceNodeBuilder constraints(NodeConstraints constraints) {
      this.constraints = constraints;
      return this;
    }

    public ReduceNodeBuilder nextNodes(List<NodeId> nextNodes) {
      this.nextNodes = nextNodes;
      return this;
    }

    public ReduceNode build() {
      return new ReduceNode(
          id, role, strategy, provider, systemPrompt, inputs, constraints, nextNodes);
    }
  }

  @Override
  public void validateReferences(Set<NodeId> allNodeIds) {
    super.validateReferences(allNodeIds);

    inputs.forEach(
        input -> {
          if (!allNodeIds.contains(input)) {
            throw new ValidationException(
                "Reduce %s references non-existent input: %s"
                    .formatted(getId().value(), input.value()));
          }
        });
  }
}
