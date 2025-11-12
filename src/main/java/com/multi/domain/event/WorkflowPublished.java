/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.event;

import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.WorkflowId;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;

/**
 * Workflow published event
 *
 * <p>Kafka topic: workflow.published.{tenantId} Partitioning: by workflowId
 */
public record WorkflowPublished(
    @NonNull String eventId,
    @NonNull WorkflowId workflowId,
    @NonNull String correlationId,
    String causationId,
    @NonNull Instant occurredAt,
    long aggregateVersion)
    implements DomainEvent {

  @FactoryMethod(description = "Create event with correlation from MDC")
  public static WorkflowPublished create(
      WorkflowId workflowId, String correlationId, long aggregateVersion) {
    return new WorkflowPublished(
        UUID.randomUUID().toString(),
        workflowId,
        correlationId,
        null,
        Instant.now(),
        aggregateVersion);
  }

  @FactoryMethod(description = "Create event with causation")
  public static WorkflowPublished createFrom(
      WorkflowId workflowId, String correlationId, String causationId, long aggregateVersion) {
    return new WorkflowPublished(
        UUID.randomUUID().toString(),
        workflowId,
        correlationId,
        causationId,
        Instant.now(),
        aggregateVersion);
  }

  @Override
  public String aggregateId() {
    return workflowId.compositeKey();
  }

  @Override
  public String aggregateType() {
    return "Workflow";
  }

  @Override
  public int eventVersion() {
    return 1;
  }

  @Override
  public String tenantId() {
    return workflowId.tenantId();
  }
}
