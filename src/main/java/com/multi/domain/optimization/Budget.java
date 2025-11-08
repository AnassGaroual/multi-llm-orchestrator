/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.optimization;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import lombok.NonNull;

/**
 * Composite budget (tokens + cost)
 *
 * <p>Thread-safe: Immutable
 */
@DomainValueObject(name = "Budget")
public record Budget(@NonNull TokenBudget tokenBudget, @NonNull CostBudget costBudget) {

  @FactoryMethod(description = "Create unlimited budget")
  public static Budget unlimited() {
    return new Budget(new TokenBudget(Integer.MAX_VALUE, 0), new CostBudget(Double.MAX_VALUE, 0));
  }

  public boolean isExhausted() {
    return tokenBudget.isExhausted() || costBudget.isExhausted();
  }

  public Budget consumeTokens(int tokens) {
    return new Budget(tokenBudget.consume(tokens), costBudget);
  }

  public Budget spendCost(double cost) {
    return new Budget(tokenBudget, costBudget.spend(cost));
  }
}
