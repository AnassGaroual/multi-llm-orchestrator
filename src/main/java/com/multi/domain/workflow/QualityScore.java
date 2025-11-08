/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.List;

/**
 * Quality score value object
 *
 * <p>Measures output quality across multiple dimensions Range: 0-10 for each dimension
 *
 * <p>Thread-safe: Immutable
 */
@DomainValueObject(name = "QualityScore")
public record QualityScore(
    double factualAccuracy,
    double coherence,
    double creativity,
    double efficiency,
    double overallScore) {

  public QualityScore {
    validateScore(factualAccuracy, "factualAccuracy");
    validateScore(coherence, "coherence");
    validateScore(creativity, "creativity");
    validateScore(efficiency, "efficiency");
  }

  @FactoryMethod(description = "Create quality score")
  public static QualityScore of(
      double factualAccuracy, double coherence, double creativity, double efficiency) {
    double overall = (factualAccuracy + coherence + creativity + efficiency) / 4.0;
    return new QualityScore(factualAccuracy, coherence, creativity, efficiency, overall);
  }

  @FactoryMethod(description = "Zero score")
  public static QualityScore zero() {
    return new QualityScore(0, 0, 0, 0, 0);
  }

  @FactoryMethod(description = "Aggregate multiple scores")
  public static QualityScore aggregate(List<QualityScore> scores) {
    if (scores.isEmpty()) {
      return zero();
    }

    double avgFactual =
        scores.stream().mapToDouble(QualityScore::factualAccuracy).average().orElse(0);
    double avgCoherence = scores.stream().mapToDouble(QualityScore::coherence).average().orElse(0);
    double avgCreativity =
        scores.stream().mapToDouble(QualityScore::creativity).average().orElse(0);
    double avgEfficiency =
        scores.stream().mapToDouble(QualityScore::efficiency).average().orElse(0);

    return of(avgFactual, avgCoherence, avgCreativity, avgEfficiency);
  }

  private static void validateScore(double score, String name) {
    if (score < 0 || score > 10) {
      throw new IllegalArgumentException(name + " must be between 0 and 10, got: " + score);
    }
  }
}
