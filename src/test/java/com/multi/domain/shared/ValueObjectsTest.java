/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Value Objects - Strong Types")
class ValueObjectsTest {

  @Nested
  @DisplayName("WorkflowId")
  class WorkflowIdTests {

    @Test
    void should_reject_null_tenant_id() {
      assertThatThrownBy(() -> new WorkflowId(null, "wf-123"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("TenantId");
    }

    @Test
    void should_reject_blank_tenant_id() {
      assertThatThrownBy(() -> new WorkflowId("  ", "wf-123"))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_null_value() {
      assertThatThrownBy(() -> new WorkflowId("tenant-1", null))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_generate_valid_uuid() {
      var id = WorkflowId.generate("tenant-1");

      assertThat(id.value()).isNotBlank().hasSize(36);
      assertThat(id.tenantId()).isEqualTo("tenant-1");
    }

    @Test
    void should_create_from_string() {
      var id = WorkflowId.of("tenant-1", "wf-123");

      assertThat(id.tenantId()).isEqualTo("tenant-1");
      assertThat(id.value()).isEqualTo("wf-123");
    }

    @Test
    void equality_should_be_based_on_composite_key() {
      var id1 = WorkflowId.of("tenant-1", "wf-123");
      var id2 = WorkflowId.of("tenant-1", "wf-123");
      var id3 = WorkflowId.of("tenant-2", "wf-123");

      assertThat(id1).isEqualTo(id2);
      assertThat(id1).isNotEqualTo(id3);
      assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void should_generate_composite_key() {
      var id = WorkflowId.of("tenant-1", "wf-123");
      assertThat(id.compositeKey()).isEqualTo("tenant-1:wf-123");
    }
  }

  @Nested
  @DisplayName("NodeId")
  class NodeIdTests {

    @Test
    void should_reject_null_value() {
      assertThatThrownBy(() -> new NodeId(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_blank_value() {
      assertThatThrownBy(() -> new NodeId("  ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_generate_valid_uuid() {
      var id = NodeId.generate();
      assertThat(id.value()).isNotBlank().hasSize(36);
    }

    @Test
    void should_create_from_string() {
      var id = NodeId.of("node-123");
      assertThat(id.value()).isEqualTo("node-123");
    }
  }

  @Nested
  @DisplayName("ExecutionId")
  class ExecutionIdTests {

    @Test
    void should_reject_null_value() {
      assertThatThrownBy(() -> new ExecutionId(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_generate_valid_uuid() {
      var id = ExecutionId.generate();
      assertThat(id.value()).isNotBlank();
    }
  }

  @Nested
  @DisplayName("ConsensusSessionId")
  class ConsensusSessionIdTests {

    @Test
    void should_reject_null_value() {
      assertThatThrownBy(() -> new ConsensusSessionId(null))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_generate_valid_uuid() {
      var id = ConsensusSessionId.generate();
      assertThat(id.value()).isNotBlank();
    }
  }
}
