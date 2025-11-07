/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.UUID;

@DomainValueObject(name = "ExecutionId")
public record ExecutionId(String value) {

  public ExecutionId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("ExecutionId cannot be blank");
    }
  }

  @FactoryMethod
  public static ExecutionId generate() {
    return new ExecutionId(UUID.randomUUID().toString());
  }

  @FactoryMethod
  public static ExecutionId of(String value) {
    return new ExecutionId(value);
  }
}
