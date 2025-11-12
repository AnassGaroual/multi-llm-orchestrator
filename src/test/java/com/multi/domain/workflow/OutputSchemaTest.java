/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OutputSchema Value Object")
class OutputSchemaTest {

  @Test
  void should_validate_required_fields() {
    var schema =
        OutputSchema.of(
            Map.of(
                "answer", "string",
                "confidence", "number"));

    Map<String, Object> validOutput = Map.of("answer", "test", "confidence", 0.95);

    assertThatCode(() -> schema.validate(validOutput)).doesNotThrowAnyException();
  }

  @Test
  void should_reject_missing_fields() {
    var schema =
        OutputSchema.of(
            Map.of(
                "answer", "string",
                "confidence", "number"));

    Map<String, Object> invalidOutput = Map.of("answer", "test");

    assertThatThrownBy(() -> schema.validate(invalidOutput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("confidence");
  }

  @Test
  void should_validate_field_types() {
    var schema = OutputSchema.of(Map.of("count", "number"));

    Map<String, Object> invalidOutput = Map.of("count", "not-a-number");

    assertThatThrownBy(() -> schema.validate(invalidOutput))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("type");
  }

  @Test
  void should_allow_any_schema() {
    var schema = OutputSchema.any();

    Map<String, Object> output = Map.of("anything", "goes");

    assertThatCode(() -> schema.validate(output)).doesNotThrowAnyException();
  }
}
