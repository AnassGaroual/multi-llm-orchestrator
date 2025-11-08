/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.event;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.ExecutionId;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;

public record ExecutionStarted(
    @NonNull String eventId,
    @NonNull ExecutionId executionId,
    @NonNull String correlationId,
    String causationId,
    @NonNull Instant occurredAt,
    long aggregateVersion,
    @NonNull String tenantId)
    implements DomainEvent {

  @FactoryMethod
  public static ExecutionStarted create(
      ExecutionId executionId, String correlationId, long aggregateVersion) {
    return new ExecutionStarted(
        UUID.randomUUID().toString(),
        executionId,
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
