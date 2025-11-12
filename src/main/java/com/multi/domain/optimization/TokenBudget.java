/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.optimization;

import com.multi.domain.annotation.DomainValueObject;

/**
 * Token budget value object
 *
 * <p>Thread-safe: Immutable
 */
@DomainValueObject(name = "TokenBudget")
public record TokenBudget(int maxTokens, int usedTokens) {

  public TokenBudget {
    if (maxTokens < 0) {
      throw new IllegalArgumentException("maxTokens must be >= 0");
    }
    if (usedTokens < 0 || usedTokens > maxTokens) {
      throw new IllegalArgumentException("usedTokens must be between 0 and maxTokens");
    }
  }

  public int remaining() {
    return maxTokens - usedTokens;
  }

  public boolean canAfford(int tokens) {
    return remaining() >= tokens;
  }

  public boolean isExhausted() {
    return remaining() <= 0;
  }

  public TokenBudget consume(int tokens) {
    if (!canAfford(tokens)) {
      throw new IllegalArgumentException(
          "Insufficient tokens: need " + tokens + ", have " + remaining());
    }
    return new TokenBudget(maxTokens, usedTokens + tokens);
  }
}
