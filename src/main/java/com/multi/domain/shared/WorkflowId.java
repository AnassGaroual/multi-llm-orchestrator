/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.UUID;

/**
 * Workflow identity with multi-tenant support
 *
 * <p>Thread-safe: Immutable record, Security: Tenant isolation built-in Interoperable:
 * JSON/Avro/Protobuf serializable
 */
@DomainValueObject(name = "WorkflowId")
public record WorkflowId(String tenantId, String value) {

  public WorkflowId {
    if (tenantId == null || tenantId.isBlank()) {
      throw new IllegalArgumentException("TenantId cannot be blank");
    }
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Value cannot be blank");
    }
  }

  @FactoryMethod(description = "Generate unique workflow ID for tenant")
  public static WorkflowId generate(String tenantId) {
    return new WorkflowId(tenantId, UUID.randomUUID().toString());
  }

  @FactoryMethod(description = "Reconstitute from storage")
  public static WorkflowId of(String tenantId, String value) {
    return new WorkflowId(tenantId, value);
  }

  /** Composite key for DB indexes and Kafka partitioning */
  public String compositeKey() {
    return tenantId + ":" + value;
  }
}
