/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.UUID;

@DomainValueObject(name = "ConsensusSessionId")
public record ConsensusSessionId(String value) {

  public ConsensusSessionId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("ConsensusSessionId cannot be blank");
    }
  }

  @FactoryMethod
  public static ConsensusSessionId generate() {
    return new ConsensusSessionId(UUID.randomUUID().toString());
  }

  @FactoryMethod
  public static ConsensusSessionId of(String value) {
    return new ConsensusSessionId(value);
  }
}
