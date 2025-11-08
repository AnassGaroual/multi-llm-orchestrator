/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NodeConstraints Value Object")
class NodeConstraintsTest {

  @Test
  void should_create_with_all_constraints() {
    var constraints =
        NodeConstraints.builder()
            .maxTokensOut(1000)
            .timeoutMs(5000)
            .temperature(0.7)
            .maxRetries(3)
            .minQualityScore(15.0)
            .build();

    assertThat(constraints.maxTokensOut()).isEqualTo(1000);
    assertThat(constraints.timeoutMs()).isEqualTo(5000);
    assertThat(constraints.temperature()).isEqualTo(0.7);
    assertThat(constraints.maxRetries()).isEqualTo(3);
    assertThat(constraints.minQualityScore()).isEqualTo(15.0);
  }

  @Test
  void should_create_with_defaults() {
    var constraints = NodeConstraints.defaults();

    assertThat(constraints.maxTokensOut()).isEqualTo(4000);
    assertThat(constraints.timeoutMs()).isEqualTo(30000);
    assertThat(constraints.temperature()).isEqualTo(1.0);
    assertThat(constraints.maxRetries()).isEqualTo(2);
    assertThat(constraints.minQualityScore()).isEqualTo(0.0);
  }

  @Test
  void should_reject_negative_max_tokens() {
    assertThatThrownBy(() -> NodeConstraints.builder().maxTokensOut(-1).build())
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_reject_invalid_temperature() {
    assertThatThrownBy(() -> NodeConstraints.builder().temperature(3.0).build())
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_reject_negative_quality_score() {
    assertThatThrownBy(() -> NodeConstraints.builder().minQualityScore(-1.0).build())
        .isInstanceOf(IllegalArgumentException.class);
  }
}
