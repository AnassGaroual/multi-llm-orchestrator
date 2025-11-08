/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.service;

import com.multi.domain.annotation.DomainService;
import com.multi.domain.shared.NodeId;
import com.multi.domain.workflow.Node;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain service: Cycle detection
 *
 * <p>Stateless operation for graph validation DFS-based cycle detection
 */
@DomainService(name = "CycleDetectionService")
public class CycleDetectionService {

  /** Detect cycles in workflow graph */
  public boolean hasCycle(Map<NodeId, Node> nodes) {
    var state = new HashMap<NodeId, Integer>();

    for (NodeId nodeId : nodes.keySet()) {
      if (hasCycleDFS(nodeId, nodes, state)) {
        return true;
      }
    }

    return false;
  }

  private boolean hasCycleDFS(NodeId current, Map<NodeId, Node> nodes, Map<NodeId, Integer> state) {
    int currentState = state.getOrDefault(current, 0);

    if (currentState == 1) return true; // Gray = visiting = cycle
    if (currentState == 2) return false; // Black = done

    state.put(current, 1); // Mark gray

    Node node = nodes.get(current);
    if (node != null) {
      for (NodeId next : node.getNextNodes()) {
        if (hasCycleDFS(next, nodes, state)) {
          return true;
        }
      }
    }

    state.put(current, 2); // Mark black
    return false;
  }
}
