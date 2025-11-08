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

@DisplayName("VoteNode")
class VoteNodeTest {

  @Test
  void should_create_vote_node() {
    var voters =
        List.of(
            new VoteNode.Voter("openai:gpt-5o", "Judge1"),
            new VoteNode.Voter("anthropic:sonnet", "Judge2"));

    var node =
        VoteNode.builder()
            .id(NodeId.of("vote"))
            .role("Consensus")
            .voters(voters)
            .ballotPrompt("Rate the output")
            .quorumPct(60)
            .minScorePerVote(12)
            .minGlobalAverage(15)
            .onFailNode(NodeId.of("retry"))
            .nextNodes(List.of())
            .build();

    assertThat(node.getVoters()).hasSize(2);
    assertThat(node.getQuorumPct()).isEqualTo(60);
  }

  @Test
  void should_require_at_least_one_voter() {
    assertThatThrownBy(
            () ->
                VoteNode.builder()
                    .id(NodeId.of("vote"))
                    .role("Consensus")
                    .voters(List.of())
                    .ballotPrompt("test")
                    .quorumPct(60)
                    .minScorePerVote(10)
                    .minGlobalAverage(12)
                    .onFailNode(NodeId.of("retry"))
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("at least one voter");
  }

  @Test
  void should_reject_invalid_quorum() {
    assertThatThrownBy(
            () ->
                VoteNode.builder()
                    .id(NodeId.of("vote"))
                    .role("Consensus")
                    .voters(List.of(new VoteNode.Voter("openai:gpt-5o", "Judge")))
                    .ballotPrompt("test")
                    .quorumPct(150)
                    .minScorePerVote(10)
                    .minGlobalAverage(12)
                    .onFailNode(NodeId.of("retry"))
                    .nextNodes(List.of())
                    .build())
        .hasMessageContaining("Quorum must be between 1-100");
  }

  @Test
  void should_use_default_values() {
    var node =
        VoteNode.builder()
            .id(NodeId.of("vote"))
            .role("Consensus")
            .voters(List.of(new VoteNode.Voter("openai:gpt-5o", "Judge")))
            .ballotPrompt("test")
            .onFailNode(NodeId.of("retry"))
            .nextNodes(List.of())
            .build();

    assertThat(node.getQuorumPct()).isEqualTo(60);
    assertThat(node.getMinScorePerVote()).isZero();
    assertThat(node.getMinGlobalAverage()).isZero();
  }
}
