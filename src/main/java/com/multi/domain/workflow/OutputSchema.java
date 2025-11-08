/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.Map;
import lombok.NonNull;

/**
 * Output schema value object
 *
 * <p>Validates node outputs against expected schema Thread-safe: Immutable
 */
@DomainValueObject(name = "OutputSchema")
public record OutputSchema(@NonNull Map<String, String> fields) {

  public OutputSchema {
    fields = Map.copyOf(fields);
  }

  @FactoryMethod(description = "Create output schema")
  public static OutputSchema of(Map<String, String> fields) {
    return new OutputSchema(fields);
  }

  @FactoryMethod(description = "Any schema (no validation)")
  public static OutputSchema any() {
    return new OutputSchema(Map.of());
  }

  /** Validate output against schema */
  public void validate(Map<String, Object> output) {
    if (fields.isEmpty()) {
      return; // Any schema
    }

    fields.forEach(
        (field, type) -> {
          if (!output.containsKey(field)) {
            throw new IllegalArgumentException("Missing required field: " + field);
          }

          var value = output.get(field);
          validateType(field, value, type);
        });
  }

  private void validateType(String field, Object value, String expectedType) {
    var valid =
        switch (expectedType.toLowerCase()) {
          case "string" -> value instanceof String;
          case "number" -> value instanceof Number;
          case "boolean" -> value instanceof Boolean;
          case "array" -> value instanceof java.util.Collection;
          case "object" -> value instanceof Map;
          default -> true;
        };

    if (!valid) {
      throw new IllegalArgumentException(
          "Field " + field + " has wrong type, expected " + expectedType);
    }
  }
}
