/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Domain Exceptions")
class DomainExceptionsTest {

  @Test
  void validation_exception_should_have_error_code() {
    var ex = new ValidationException("Invalid input");

    assertThat(ex.getErrorCode()).isEqualTo("DOMAIN_VALIDATION_ERROR");
    assertThat(ex.getMessage()).isEqualTo("Invalid input");
  }

  @Test
  void cycle_detected_exception_should_contain_node_id() {
    var ex = new CycleDetectedException("node-123");

    assertThat(ex.getErrorCode()).isEqualTo("WORKFLOW_CYCLE");
    assertThat(ex.getMessage()).contains("node-123");
  }

  @Test
  void quorum_not_reached_exception_should_show_counts() {
    var ex = new QuorumNotReachedException(2, 5);

    assertThat(ex.getErrorCode()).isEqualTo("QUORUM_NOT_REACHED");
    assertThat(ex.getMessage()).contains("2/5");
  }

  @Test
  void insufficient_budget_exception_should_show_details() {
    var ex = new InsufficientBudgetException("tokens", 1000, 500);

    assertThat(ex.getErrorCode()).isEqualTo("INSUFFICIENT_BUDGET");
    assertThat(ex.getMessage()).contains("tokens");
    assertThat(ex.getMessage()).contains("1000");
    assertThat(ex.getMessage()).contains("500");
  }

  @Test
  void invalid_topology_exception_should_have_reason() {
    var ex = new InvalidTopologyException("disconnected nodes");

    assertThat(ex.getErrorCode()).isEqualTo("INVALID_TOPOLOGY");
    assertThat(ex.getMessage()).contains("disconnected nodes");
  }

  @Test
  void domain_exception_should_be_sealed() {
    assertThat(DomainException.class.isSealed()).isTrue();
  }

  @Test
  void domain_exception_should_be_runtime() {
    var ex = new ValidationException("test");
    assertThat(ex).isInstanceOf(RuntimeException.class);
  }
}
