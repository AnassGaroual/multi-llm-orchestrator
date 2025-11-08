/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.annotation;

import java.lang.annotation.*;

/**
 * Marks a package as a Bounded Context in Domain-Driven Design
 *
 * <p>A Bounded Context defines clear boundaries within which a particular domain model is defined
 * and applicable.
 *
 * <p>Usage: Place on package-info.java
 *
 * <p>Example:
 *
 * <pre>
 * @BoundedContext(
 *     name = "Order Management",
 *     description = "Handles order placement, fulfillment, and tracking"
 * )
 * package com.example.orders;
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface BoundedContext {

  /** Name of the bounded context */
  String name();

  /** Description of the bounded context's responsibilities */
  String description() default "";
}
