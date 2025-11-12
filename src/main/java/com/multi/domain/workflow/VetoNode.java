/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.*;

/**
 * Veto node: Quality gate Applies validation rules to outputs Triggers retry/fallback on failure
 */
@Value
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class VetoNode extends Node {

  Map<String, Object> rules;
  NodeId onFailNode;

  private VetoNode(
      NodeId id,
      String role,
      Map<String, Object> rules,
      NodeId onFailNode,
      List<NodeId> nextNodes) {
    super(id, role, nextNodes);

    if (rules == null || rules.isEmpty()) {
      throw new ValidationException("Veto must have at least one rule");
    }

    this.rules = Map.copyOf(rules);
    this.onFailNode = onFailNode;
  }

  @FactoryMethod
  public static VetoNodeBuilder builder() {
    return new VetoNodeBuilder();
  }

  public static class VetoNodeBuilder {
    private NodeId id;
    private String role;
    private Map<String, Object> rules;
    private NodeId onFailNode;
    private List<NodeId> nextNodes = List.of();

    public VetoNodeBuilder id(NodeId id) {
      this.id = id;
      return this;
    }

    public VetoNodeBuilder role(String role) {
      this.role = role;
      return this;
    }

    public VetoNodeBuilder rules(Map<String, Object> rules) {
      this.rules = rules;
      return this;
    }

    public VetoNodeBuilder onFailNode(NodeId onFailNode) {
      this.onFailNode = onFailNode;
      return this;
    }

    public VetoNodeBuilder nextNodes(List<NodeId> nextNodes) {
      this.nextNodes = nextNodes;
      return this;
    }

    public VetoNode build() {
      return new VetoNode(id, role, rules, onFailNode, nextNodes);
    }
  }

  @Override
  public void validateReferences(Set<NodeId> allNodeIds) {
    super.validateReferences(allNodeIds);

    if (!allNodeIds.contains(onFailNode)) {
      throw new ValidationException(
          "Veto %s references non-existent onFail node: %s"
              .formatted(getId().value(), onFailNode.value()));
    }
  }
}
