/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.execution;

/**
 * Execution strategy
 *
 * <p>SEQUENTIAL: One node at a time PARALLEL: All independent nodes concurrently (Virtual Threads)
 * SPECULATIVE: Launch multiple variants, kill losers
 */
public enum ExecutionStrategy {
  SEQUENTIAL,
  PARALLEL,
  SPECULATIVE
}
