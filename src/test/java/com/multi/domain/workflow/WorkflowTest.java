/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import com.multi.domain.shared.*;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Workflow Aggregate")
class WorkflowTest {

  @Nested
  @DisplayName("Creation")
  class Creation {

    @Test
    void should_create_workflow_in_draft() {
      var workflow = Workflow.define("tenant-1", "Test Workflow");

      assertThat(workflow.getId()).isNotNull();
      assertThat(workflow.getId().tenantId()).isEqualTo("tenant-1");
      assertThat(workflow.getName()).isEqualTo("Test Workflow");
      assertThat(workflow.getStatus()).isEqualTo(WorkflowStatus.DRAFT);
      assertThat(workflow.getNodes()).isEmpty();
      assertThat(workflow.getAggregateVersion()).isZero();
    }

    @Test
    void should_generate_unique_ids() {
      var w1 = Workflow.define("tenant-1", "Workflow 1");
      var w2 = Workflow.define("tenant-1", "Workflow 2");

      assertThat(w1.getId()).isNotEqualTo(w2.getId());
    }
  }

  @Nested
  @DisplayName("Node Management")
  class NodeManagement {

    @Test
    void should_add_node() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("node1");

      var node = createTestNode(nodeId);

      var updated = workflow.withNode(node);

      assertThat(updated.getNodes()).hasSize(1);
      assertThat(updated.getNodes()).containsKey(nodeId);
    }

    @Test
    void should_reject_duplicate_node_id() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("node1");

      var node1 = createTestNode(nodeId);
      var node2 = createTestNode(nodeId);

      var updated = workflow.withNode(node1);

      assertThatThrownBy(() -> updated.withNode(node2))
          .isInstanceOf(ValidationException.class)
          .hasMessageContaining("already exists");
    }

    @Test
    void should_prevent_adding_to_published() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("node1");
      var node = createTestNode(nodeId);

      var updated = workflow.withNode(node).withEntryNode(nodeId);

      var result = updated.publish("corr-123");
      var published = result.workflow();

      var newNode = createTestNode(NodeId.of("node2"));

      assertThatThrownBy(() -> published.withNode(newNode))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("published");
    }
  }

  @Nested
  @DisplayName("Entry Node")
  class EntryNodeTests {

    @Test
    void should_set_entry_node() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("entry");
      var node = createTestNode(nodeId);

      var updated = workflow.withNode(node).withEntryNode(nodeId);

      assertThat(updated.getEntryNode()).isEqualTo(nodeId);
    }

    @Test
    void should_reject_non_existent_entry() {
      var workflow = Workflow.define("tenant-1", "Test");
      var ghost = NodeId.of("ghost");

      assertThatThrownBy(() -> workflow.withEntryNode(ghost))
          .isInstanceOf(ValidationException.class)
          .hasMessageContaining("does not exist");
    }
  }

  @Nested
  @DisplayName("Validation")
  class Validation {

    @Test
    void should_require_entry_node() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("node1");
      var node = createTestNode(nodeId);

      var updated = workflow.withNode(node);

      assertThatThrownBy(updated::validate)
          .isInstanceOf(ValidationException.class)
          .hasMessageContaining("Entry node not defined");
    }

    @Test
    void should_require_at_least_one_node() {
      var workflow = Workflow.define("tenant-1", "Test");

      assertThatThrownBy(workflow::validate)
          .isInstanceOf(ValidationException.class)
          .hasMessageContaining("no nodes");
    }

    @Test
    void should_detect_cycle() {
      var workflow = Workflow.define("tenant-1", "Test");

      var n1 = NodeId.of("n1");
      var n2 = NodeId.of("n2");

      var node1 =
          AgentNode.builder()
              .id(n1)
              .role("A")
              .provider("openai:gpt-5o")
              .systemPrompt("test")
              .constraints(NodeConstraints.defaults())
              .inputMapping(InputMapping.passthrough())
              .outputSchema(OutputSchema.any())
              .nextNodes(List.of(n2))
              .build();

      var node2 =
          AgentNode.builder()
              .id(n2)
              .role("B")
              .provider("openai:gpt-5o")
              .systemPrompt("test")
              .constraints(NodeConstraints.defaults())
              .inputMapping(InputMapping.passthrough())
              .outputSchema(OutputSchema.any())
              .nextNodes(List.of(n1))
              .build();

      var updated = workflow.withNode(node1).withNode(node2).withEntryNode(n1);

      assertThatThrownBy(updated::validate)
          .isInstanceOf(CycleDetectedException.class)
          .hasMessageContaining("n1");
    }

    @Test
    void should_accept_valid_dag() {
      var workflow = Workflow.define("tenant-1", "Test");

      var n1 = NodeId.of("n1");
      var n2 = NodeId.of("n2");
      var n3 = NodeId.of("n3");

      var node1 = createTestNodeWithNext(n1, List.of(n2, n3));
      var node2 = createTestNode(n2);
      var node3 = createTestNode(n3);

      var updated = workflow.withNode(node1).withNode(node2).withNode(node3).withEntryNode(n1);

      assertThatCode(updated::validate).doesNotThrowAnyException();
    }
  }

  @Nested
  @DisplayName("Publication")
  class Publication {

    @Test
    void should_publish_valid_workflow() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("node1");
      var node = createTestNode(nodeId);

      var updated = workflow.withNode(node).withEntryNode(nodeId);

      var result = updated.publish("corr-123");

      assertThat(result.workflow().getStatus()).isEqualTo(WorkflowStatus.PUBLISHED);
      assertThat(result.workflow().getAggregateVersion()).isEqualTo(1L);
      assertThat(result.event()).isNotNull();
      assertThat(result.event().workflowId()).isEqualTo(updated.getId());
      assertThat(result.event().correlationId()).isEqualTo("corr-123");
    }

    @Test
    void should_validate_before_publication() {
      var workflow = Workflow.define("tenant-1", "Test");

      assertThatThrownBy(() -> workflow.publish("corr-123"))
          .isInstanceOf(ValidationException.class);
    }
  }

  @Nested
  @DisplayName("Immutability")
  class Immutability {

    @Test
    void should_return_new_instance_on_update() {
      var workflow = Workflow.define("tenant-1", "Test");
      var node = createTestNode(NodeId.of("node1"));

      var updated = workflow.withNode(node);

      assertThat(workflow.getNodes()).isEmpty();
      assertThat(updated.getNodes()).hasSize(1);
    }

    @Test
    void should_not_mutate_original_on_publish() {
      var workflow = Workflow.define("tenant-1", "Test");
      var nodeId = NodeId.of("node1");
      var node = createTestNode(nodeId);

      var draft = workflow.withNode(node).withEntryNode(nodeId);

      var result = draft.publish("corr-123");

      assertThat(draft.getStatus()).isEqualTo(WorkflowStatus.DRAFT);
      assertThat(result.workflow().getStatus()).isEqualTo(WorkflowStatus.PUBLISHED);
    }
  }

  // Helper methods
  private AgentNode createTestNode(NodeId id) {
    return AgentNode.builder()
        .id(id)
        .role("Test")
        .provider("openai:gpt-5o")
        .systemPrompt("test")
        .constraints(NodeConstraints.defaults())
        .inputMapping(InputMapping.passthrough())
        .outputSchema(OutputSchema.any())
        .nextNodes(List.of())
        .build();
  }

  private AgentNode createTestNodeWithNext(NodeId id, List<NodeId> next) {
    return AgentNode.builder()
        .id(id)
        .role("Test")
        .provider("openai:gpt-5o")
        .systemPrompt("test")
        .constraints(NodeConstraints.defaults())
        .inputMapping(InputMapping.passthrough())
        .outputSchema(OutputSchema.any())
        .nextNodes(next)
        .build();
  }
}
