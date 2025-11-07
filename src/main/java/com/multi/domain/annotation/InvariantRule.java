/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface InvariantRule {
  String value();

  Severity severity() default Severity.ERROR;

  enum Severity {
    WARNING,
    ERROR,
    CRITICAL
  }
}
