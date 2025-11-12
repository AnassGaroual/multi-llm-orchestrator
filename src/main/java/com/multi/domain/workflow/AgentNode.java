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
 * Agent node: Executes LLM call
 *
 * <p>Security: Provider validation Thread-safe: Immutable
 */
@Value
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class AgentNode extends Node {

  String provider;
  String systemPrompt;
  NodeConstraints constraints;
  InputMapping inputMapping;
  OutputSchema outputSchema;

  private AgentNode(
      NodeId id,
      String role,
      String provider,
      String systemPrompt,
      NodeConstraints constraints,
      InputMapping inputMapping,
      OutputSchema outputSchema,
      List<NodeId> nextNodes) {
    super(id, role, nextNodes);

    // Validation
    if (provider == null || !provider.matches("^(openai|anthropic|mistral|ollama):.+$")) {
      throw new ValidationException("Invalid provider format: " + provider);
    }

    this.provider = provider;
    this.systemPrompt = systemPrompt;
    this.constraints = constraints;
    this.inputMapping = inputMapping;
    this.outputSchema = outputSchema;
  }

  @FactoryMethod(description = "Create agent node builder")
  public static AgentNodeBuilder builder() {
    return new AgentNodeBuilder();
  }

  public static class AgentNodeBuilder {
    private NodeId id;
    private String role;
    private String provider;
    private String systemPrompt;
    private NodeConstraints constraints;
    private InputMapping inputMapping;
    private OutputSchema outputSchema;
    private List<NodeId> nextNodes = List.of();

    public AgentNodeBuilder id(NodeId id) {
      this.id = id;
      return this;
    }

    public AgentNodeBuilder role(String role) {
      this.role = role;
      return this;
    }

    public AgentNodeBuilder provider(String provider) {
      this.provider = provider;
      return this;
    }

    public AgentNodeBuilder systemPrompt(String systemPrompt) {
      this.systemPrompt = systemPrompt;
      return this;
    }

    public AgentNodeBuilder constraints(NodeConstraints constraints) {
      this.constraints = constraints;
      return this;
    }

    public AgentNodeBuilder inputMapping(InputMapping inputMapping) {
      this.inputMapping = inputMapping;
      return this;
    }

    public AgentNodeBuilder outputSchema(OutputSchema outputSchema) {
      this.outputSchema = outputSchema;
      return this;
    }

    public AgentNodeBuilder nextNodes(List<NodeId> nextNodes) {
      this.nextNodes = nextNodes;
      return this;
    }

    public AgentNode build() {
      return new AgentNode(
          id, role, provider, systemPrompt, constraints, inputMapping, outputSchema, nextNodes);
    }
  }
}
