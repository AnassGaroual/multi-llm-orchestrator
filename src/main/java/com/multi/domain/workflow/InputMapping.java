/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.domain.workflow;

import com.multi.domain.annotation.DomainValueObject;
import com.multi.domain.annotation.FactoryMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.NonNull;

/**
 * Input mapping value object
 *
 * <p>Maps context variables to node inputs Supports template syntax: {{path.to.value}}
 *
 * <p>Thread-safe: Immutable
 */
@DomainValueObject(name = "InputMapping")
public record InputMapping(@NonNull Map<String, String> mappings) {

  private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\{([^}]+)}}");

  public InputMapping {
    mappings = Map.copyOf(mappings);
  }

  @FactoryMethod(description = "Create input mapping")
  public static InputMapping of(Map<String, String> mappings) {
    return new InputMapping(mappings);
  }

  @FactoryMethod(description = "Passthrough mapping (identity)")
  public static InputMapping passthrough() {
    return new InputMapping(Map.of());
  }

  /** Apply mapping to context */
  public Map<String, Object> apply(Map<String, Object> context) {
    if (mappings.isEmpty()) {
      return context;
    }

    var result = new HashMap<String, Object>();

    mappings.forEach(
        (key, template) -> {
          var value = resolveTemplate(template, context);
          result.put(key, value);
        });

    return result;
  }

  private Object resolveTemplate(String template, Map<String, Object> context) {
    var matcher = TEMPLATE_PATTERN.matcher(template);

    if (matcher.matches()) {
      var path = matcher.group(1);
      return resolvePath(path, context);
    }

    return template;
  }

  @SuppressWarnings("unchecked")
  private Object resolvePath(String path, Map<String, Object> context) {
    var parts = path.split("\\.");
    Object current = context;

    for (var part : parts) {
      if (current instanceof Map) {
        current = ((Map<String, Object>) current).get(part);
        if (current == null) {
          return "{{" + path + "}}";
        }
      } else {
        return "{{" + path + "}}";
      }
    }

    return current;
  }
}
