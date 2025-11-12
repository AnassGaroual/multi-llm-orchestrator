/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("QualityScore Value Object")
class QualityScoreTest {

  @Test
  void should_calculate_overall_score() {
    var score = QualityScore.of(8.0, 9.0, 7.0, 8.5);

    assertThat(score.overallScore()).isCloseTo(8.125, within(0.01));
  }

  @Test
  void should_aggregate_multiple_scores() {
    var scores =
        List.of(
            QualityScore.of(8.0, 9.0, 7.0, 8.0),
            QualityScore.of(7.0, 8.0, 9.0, 7.5),
            QualityScore.of(9.0, 8.5, 8.0, 9.0));

    var aggregated = QualityScore.aggregate(scores);

    assertThat(aggregated.factualAccuracy()).isCloseTo(8.0, within(0.1));
    assertThat(aggregated.coherence()).isCloseTo(8.5, within(0.1));
    assertThat(aggregated.creativity()).isCloseTo(8.0, within(0.1));
    assertThat(aggregated.efficiency()).isCloseTo(8.16, within(0.1));
  }

  @Test
  void should_reject_negative_scores() {
    assertThatThrownBy(() -> QualityScore.of(-1.0, 8.0, 7.0, 8.0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_reject_scores_above_10() {
    assertThatThrownBy(() -> QualityScore.of(8.0, 11.0, 7.0, 8.0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void should_create_default_zero_score() {
    var score = QualityScore.zero();

    assertThat(score.factualAccuracy()).isZero();
    assertThat(score.coherence()).isZero();
    assertThat(score.overallScore()).isZero();
  }
}
