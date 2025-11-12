/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

public final class InsufficientBudgetException extends DomainException {

  public InsufficientBudgetException(String resourceType, int required, int available) {
    super(
        "INSUFFICIENT_BUDGET",
        "Insufficient %s: need %d, have %d".formatted(resourceType, required, available));
  }
}
