/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.event;

import static org.assertj.core.api.Assertions.*;

import com.multi.domain.shared.*;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Domain Events - Kafka Ready")
class DomainEventTest {

  @Test
  void workflow_published_should_have_all_metadata() {
    var workflowId = WorkflowId.of("tenant-1", "wf-123");
    var correlationId = "corr-456";

    var event = WorkflowPublished.create(workflowId, correlationId, 1L);

    assertThat(event.eventId()).isNotBlank();
    assertThat(event.workflowId()).isEqualTo(workflowId);
    assertThat(event.aggregateId()).isEqualTo("tenant-1:wf-123");
    assertThat(event.aggregateType()).isEqualTo("Workflow");
    assertThat(event.eventVersion()).isEqualTo(1);
    assertThat(event.correlationId()).isEqualTo(correlationId);
    assertThat(event.causationId()).isNull();
    assertThat(event.occurredAt()).isNotNull();
    assertThat(event.aggregateVersion()).isEqualTo(1L);
    assertThat(event.tenantId()).isEqualTo("tenant-1");
  }

  @Test
  void event_should_generate_unique_event_id() {
    var wfId = WorkflowId.of("tenant-1", "wf-123");

    var e1 = WorkflowPublished.create(wfId, "corr-1", 1L);
    var e2 = WorkflowPublished.create(wfId, "corr-1", 1L);

    assertThat(e1.eventId()).isNotEqualTo(e2.eventId());
  }

  @Test
  void event_occurred_at_should_be_recent() {
    var wfId = WorkflowId.of("tenant-1", "wf-123");

    var before = Instant.now();
    var event = WorkflowPublished.create(wfId, "corr-1", 1L);
    var after = Instant.now();

    assertThat(event.occurredAt()).isBetween(before, after);
  }

  @Test
  void event_with_causation_should_have_causation_id() {
    var wfId = WorkflowId.of("tenant-1", "wf-123");

    var event = WorkflowPublished.createFrom(wfId, "corr-1", "cause-1", 2L);

    assertThat(event.causationId()).isEqualTo("cause-1");
    assertThat(event.aggregateVersion()).isEqualTo(2L);
  }

  @Test
  void execution_started_should_have_metadata() {
    var execId = ExecutionId.generate();
    var correlationId = "corr-789";

    var event = ExecutionStarted.create(execId, correlationId, 1L);

    assertThat(event.eventId()).isNotBlank();
    assertThat(event.executionId()).isEqualTo(execId);
    assertThat(event.aggregateId()).isEqualTo(execId.value());
    assertThat(event.aggregateType()).isEqualTo("Execution");
    assertThat(event.correlationId()).isEqualTo(correlationId);
  }

  @Test
  void node_executed_should_have_node_reference() {
    var execId = ExecutionId.generate();
    var nodeId = NodeId.of("node-1");

    var event = NodeExecuted.create(execId, nodeId, "corr-1", 3L);

    assertThat(event.nodeId()).isEqualTo(nodeId);
    assertThat(event.executionId()).isEqualTo(execId);
    assertThat(event.aggregateVersion()).isEqualTo(3L);
  }

  @Test
  void consensus_achieved_should_have_session_reference() {
    var sessionId = ConsensusSessionId.generate();

    var event = ConsensusAchieved.create(sessionId, "corr-1", 1L);

    assertThat(event.sessionId()).isEqualTo(sessionId);
    assertThat(event.aggregateType()).isEqualTo("ConsensusSession");
  }
}
