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
 * Vote node: Consensus voting
 *
 * <p>Multiple LLM judges vote on output quality Requires quorum to pass
 */
@Value
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class VoteNode extends Node {

  public record Voter(String provider, String role) {}

  List<Voter> voters;
  String ballotPrompt;
  int quorumPct;
  int minScorePerVote;
  int minGlobalAverage;
  NodeId onFailNode;

  private VoteNode(
      NodeId id,
      String role,
      List<Voter> voters,
      String ballotPrompt,
      int quorumPct,
      int minScorePerVote,
      int minGlobalAverage,
      NodeId onFailNode,
      List<NodeId> nextNodes) {
    super(id, role, nextNodes);

    if (voters == null || voters.isEmpty()) {
      throw new ValidationException("Vote must have at least one voter");
    }

    if (quorumPct < 1 || quorumPct > 100) {
      throw new ValidationException("Quorum must be between 1-100");
    }

    if (minScorePerVote < 0 || minScorePerVote > 20) {
      throw new ValidationException("Min score per vote must be between 0-20");
    }

    this.voters = List.copyOf(voters);
    this.ballotPrompt = ballotPrompt;
    this.quorumPct = quorumPct;
    this.minScorePerVote = minScorePerVote;
    this.minGlobalAverage = minGlobalAverage;
    this.onFailNode = onFailNode;
  }

  @FactoryMethod
  public static VoteNodeBuilder builder() {
    return new VoteNodeBuilder();
  }

  public static class VoteNodeBuilder {
    private NodeId id;
    private String role;
    private List<Voter> voters;
    private String ballotPrompt;
    private int quorumPct = 60; // Default
    private int minScorePerVote = 0; // Default
    private int minGlobalAverage = 0; // Default
    private NodeId onFailNode;
    private List<NodeId> nextNodes = List.of();

    public VoteNodeBuilder id(NodeId id) {
      this.id = id;
      return this;
    }

    public VoteNodeBuilder role(String role) {
      this.role = role;
      return this;
    }

    public VoteNodeBuilder voters(List<Voter> voters) {
      this.voters = voters;
      return this;
    }

    public VoteNodeBuilder ballotPrompt(String ballotPrompt) {
      this.ballotPrompt = ballotPrompt;
      return this;
    }

    public VoteNodeBuilder quorumPct(int quorumPct) {
      this.quorumPct = quorumPct;
      return this;
    }

    public VoteNodeBuilder minScorePerVote(int minScorePerVote) {
      this.minScorePerVote = minScorePerVote;
      return this;
    }

    public VoteNodeBuilder minGlobalAverage(int minGlobalAverage) {
      this.minGlobalAverage = minGlobalAverage;
      return this;
    }

    public VoteNodeBuilder onFailNode(NodeId onFailNode) {
      this.onFailNode = onFailNode;
      return this;
    }

    public VoteNodeBuilder nextNodes(List<NodeId> nextNodes) {
      this.nextNodes = nextNodes;
      return this;
    }

    public VoteNode build() {
      return new VoteNode(
          id,
          role,
          voters,
          ballotPrompt,
          quorumPct,
          minScorePerVote,
          minGlobalAverage,
          onFailNode,
          nextNodes);
    }
  }

  @Override
  public void validateReferences(Set<NodeId> allNodeIds) {
    super.validateReferences(allNodeIds);

    if (!allNodeIds.contains(onFailNode)) {
      throw new ValidationException(
          "Vote %s references non-existent onFail node: %s"
              .formatted(getId().value(), onFailNode.value()));
    }
  }
}
