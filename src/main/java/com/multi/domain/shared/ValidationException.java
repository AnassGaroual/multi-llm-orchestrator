/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.shared;

public final class ValidationException extends DomainException {

  public ValidationException(String message) {
    super("DOMAIN_VALIDATION_ERROR", message);
  }

  public ValidationException(String message, Throwable cause) {
    super("DOMAIN_VALIDATION_ERROR", message, cause);
  }
}
