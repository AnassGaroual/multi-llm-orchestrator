/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.event;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.ConsensusSessionId;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;

public record ConsensusAchieved(
    @NonNull String eventId,
    @NonNull ConsensusSessionId sessionId,
    @NonNull String correlationId,
    String causationId,
    @NonNull Instant occurredAt,
    long aggregateVersion,
    @NonNull String tenantId)
    implements DomainEvent {

  @FactoryMethod
  public static ConsensusAchieved create(
      ConsensusSessionId sessionId, String correlationId, long aggregateVersion) {
    return new ConsensusAchieved(
        UUID.randomUUID().toString(),
        sessionId,
        correlationId,
        null,
        Instant.now(),
        aggregateVersion,
        "default");
  }

  @Override
  public String aggregateId() {
    return sessionId.value();
  }

  @Override
  public String aggregateType() {
    return "ConsensusSession";
  }

  @Override
  public int eventVersion() {
    return 1;
  }
}
