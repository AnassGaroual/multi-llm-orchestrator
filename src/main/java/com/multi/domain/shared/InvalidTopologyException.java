/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

public final class InvalidTopologyException extends DomainException {

  public InvalidTopologyException(String reason) {
    super("INVALID_TOPOLOGY", "Invalid workflow topology: " + reason);
  }
}
