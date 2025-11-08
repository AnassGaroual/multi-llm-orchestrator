/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.optimization;

import com.multi.domain.annotation.DomainValueObject;

/**
 * Cost budget value object (USD)
 *
 * <p>Thread-safe: Immutable
 */
@DomainValueObject(name = "CostBudget")
public record CostBudget(double maxCost, double usedCost) {

  public CostBudget {
    if (maxCost < 0) {
      throw new IllegalArgumentException("maxCost must be >= 0");
    }
    if (usedCost < 0 || usedCost > maxCost) {
      throw new IllegalArgumentException("usedCost must be between 0 and maxCost");
    }
  }

  public double remaining() {
    return maxCost - usedCost;
  }

  public boolean canAfford(double cost) {
    return remaining() >= cost;
  }

  public boolean isExhausted() {
    return remaining() <= 0.0;
  }

  public CostBudget spend(double cost) {
    if (!canAfford(cost)) {
      throw new IllegalArgumentException(
          "Insufficient cost: need " + cost + ", have " + remaining());
    }
    return new CostBudget(maxCost, usedCost + cost);
  }
}
