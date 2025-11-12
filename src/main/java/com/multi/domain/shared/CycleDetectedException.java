/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

public final class CycleDetectedException extends DomainException {

  public CycleDetectedException(String nodeId) {
    super("WORKFLOW_CYCLE", "Cycle detected at node: " + nodeId);
  }
}
