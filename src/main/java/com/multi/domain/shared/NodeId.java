/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.UUID;

@DomainValueObject(name = "NodeId")
public record NodeId(String value) {

  public NodeId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("NodeId cannot be blank");
    }
  }

  @FactoryMethod
  public static NodeId generate() {
    return new NodeId(UUID.randomUUID().toString());
  }

  @FactoryMethod
  public static NodeId of(String value) {
    return new NodeId(value);
  }
}
