/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.execution;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import com.multi.domain.shared.NodeId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.NonNull;

/**
 * Execution context value object
 *
 * <p>Holds execution state (variables, results) Thread-safe: Immutable (builder pattern for
 * updates)
 *
 * <p>CRITICAL for Virtual Threads: No concurrent modification
 */
@DomainValueObject(name = "ExecutionContext")
public record ExecutionContext(
    @NonNull Map<String, Object> variables,
    @NonNull Map<NodeId, Map<String, Object>> results,
    @NonNull String correlationId) {

  private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

  public ExecutionContext {
    variables = Map.copyOf(variables);
    results = Map.copyOf(results);
  }

  @FactoryMethod(description = "Create initial context")
  public static ExecutionContext initial(Map<String, Object> userInputs) {
    var vars = new HashMap<String, Object>();
    vars.put("user", userInputs);

    return new ExecutionContext(vars, Map.of(), UUID.randomUUID().toString());
  }

  /** Immutable update: add variable */
  public ExecutionContext withVariable(String key, Object value) {
    var newVars = new HashMap<>(variables);
    newVars.put(key, value);
    return new ExecutionContext(newVars, results, correlationId);
  }

  /** Immutable update: add result */
  public ExecutionContext withResult(NodeId nodeId, Map<String, Object> result) {
    var newResults = new HashMap<>(results);
    newResults.put(nodeId, Map.copyOf(result));
    return new ExecutionContext(variables, newResults, correlationId);
  }

  /** Get variable by path (e.g., "user.goal") */
  public Object getVariable(String path) {
    return resolvePath(path, variables);
  }

  /** Get result for node */
  public Map<String, Object> getResult(NodeId nodeId) {
    return results.get(nodeId);
  }

  /** Render template with variable substitution */
  public String renderTemplate(String template) {
    if (template == null) return "";

    var result = template;
    var matcher = TEMPLATE_PATTERN.matcher(template);

    while (matcher.find()) {
      var path = matcher.group(1);
      var value = resolvePath(path, variables);

      if (value != null) {
        result = result.replace("{{" + path + "}}", String.valueOf(value));
      }
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  private Object resolvePath(String path, Map<String, Object> context) {
    var parts = path.split("\\.");
    Object current = context;

    for (var part : parts) {
      if (current instanceof Map) {
        current = ((Map<String, Object>) current).get(part);
        if (current == null) {
          return null;
        }
      } else {
        return null;
      }
    }

    return current;
  }
}
