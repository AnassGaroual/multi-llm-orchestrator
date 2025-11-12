/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.event;

import java.time.Instant;

/**
 * Domain event contract
 *
 * <p>Kafka: Complete metadata for distributed tracing Event Sourcing: Aggregate versioning
 * Integration: Compatible with CorrelationIdFilter CloudEvents spec compliant
 */
public sealed interface DomainEvent
    permits WorkflowPublished, ExecutionStarted, NodeExecuted, ConsensusAchieved {

  String eventId();

  String aggregateId();

  String aggregateType();

  int eventVersion();

  String correlationId();

  String causationId();

  Instant occurredAt();

  long aggregateVersion();

  String tenantId();
}
