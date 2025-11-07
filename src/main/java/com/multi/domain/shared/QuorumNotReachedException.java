/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

public final class QuorumNotReachedException extends DomainException {

  public QuorumNotReachedException(int actual, int required) {
    super(
        "QUORUM_NOT_REACHED",
        "Quorum not reached: %d/%d votes received".formatted(actual, required));
  }
}
