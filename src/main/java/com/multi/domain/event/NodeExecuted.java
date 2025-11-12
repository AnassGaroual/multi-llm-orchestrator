/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.event;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.*;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;

public record NodeExecuted(
    @NonNull String eventId,
    @NonNull ExecutionId executionId,
    @NonNull NodeId nodeId,
    @NonNull String correlationId,
    String causationId,
    @NonNull Instant occurredAt,
    long aggregateVersion,
    @NonNull String tenantId)
    implements DomainEvent {

  @FactoryMethod
  public static NodeExecuted create(
      ExecutionId executionId, NodeId nodeId, String correlationId, long aggregateVersion) {
    return new NodeExecuted(
        UUID.randomUUID().toString(),
        executionId,
        nodeId,
        correlationId,
        null,
        Instant.now(),
        aggregateVersion,
        "default");
  }

  @Override
  public String aggregateId() {
    return executionId.value();
  }

  @Override
  public String aggregateType() {
    return "Execution";
  }

  @Override
  public int eventVersion() {
    return 1;
  }
}
