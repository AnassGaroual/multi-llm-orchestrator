/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

/**
 * Base domain exception - Pure Java (zero framework dependencies)
 *
 * <p>Clean Architecture: Domain is innermost circle Security: Error codes prevent information
 * leakage Thread-safe: Immutable exception
 */
public sealed class DomainException extends RuntimeException
    permits ValidationException,
        CycleDetectedException,
        QuorumNotReachedException,
        InsufficientBudgetException,
        InvalidTopologyException {

  private final String errorCode;

  protected DomainException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  protected DomainException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
