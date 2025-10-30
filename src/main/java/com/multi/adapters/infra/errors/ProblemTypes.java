/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.adapters.infra.errors;

import java.net.URI;

/** Central registry of problem type URNs (RFC 9457 friendly, no real domain required). */
public enum ProblemTypes {
  VALIDATION("urn:problem:validation-error"),
  CONSTRAINT("urn:problem:constraint-violation"),
  NOT_FOUND("urn:problem:not-found"),
  METHOD_NOT_ALLOWED("urn:problem:method-not-allowed"),
  UNSUPPORTED_MEDIA("urn:problem:unsupported-media-type"),
  BAD_REQUEST("urn:problem:bad-request"),
  FORBIDDEN("urn:problem:forbidden"),
  UNAUTHORIZED("urn:problem:unauthorized"),
  CONFLICT("urn:problem:conflict"),
  RATE_LIMIT("urn:problem:rate-limit"),
  INTERNAL("urn:problem:internal");

  private final URI uri;

  ProblemTypes(String value) {
    this.uri = URI.create(value);
  }

  public URI uri() {
    return uri;
  }
}
