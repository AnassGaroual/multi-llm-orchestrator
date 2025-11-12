/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import lombok.Builder;

/**
 * Node execution constraints
 *
 * <p>Thread-safe: Immutable
 */
@Builder(toBuilder = true)
@DomainValueObject(name = "NodeConstraints")
public record NodeConstraints(
    int maxTokensOut, int timeoutMs, double temperature, int maxRetries, double minQualityScore) {

  public NodeConstraints {
    if (maxTokensOut <= 0) {
      throw new IllegalArgumentException("maxTokensOut must be > 0");
    }
    if (timeoutMs <= 0) {
      throw new IllegalArgumentException("timeoutMs must be > 0");
    }
    if (temperature < 0 || temperature > 2.0) {
      throw new IllegalArgumentException("temperature must be between 0 and 2.0");
    }
    if (maxRetries < 0) {
      throw new IllegalArgumentException("maxRetries must be >= 0");
    }
    if (minQualityScore < 0 || minQualityScore > 20) {
      throw new IllegalArgumentException("minQualityScore must be between 0 and 20");
    }
  }

  @FactoryMethod(description = "Default constraints")
  public static NodeConstraints defaults() {
    return NodeConstraints.builder()
        .maxTokensOut(4000)
        .timeoutMs(30000)
        .temperature(1.0)
        .maxRetries(2)
        .minQualityScore(0.0)
        .build();
  }
}
