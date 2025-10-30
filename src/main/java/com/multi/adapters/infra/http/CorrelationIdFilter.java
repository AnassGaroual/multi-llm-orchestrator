/*
 * Copyright (c) 2025 Anass Garoual
 * Licensed under the MIT License.
 */
package com.multi.adapters.infra.http;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Ensures each request has a correlation id. - Reads from header if present (idempotent), else
 * generates. - Sets MDC and echoes header in the response.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CorrelationIdFilter implements Filter {

  public static final String MDC_KEY = "correlationId";

  @Value("${app.http.correlation-header:X-Correlation-Id}")
  private String headerName;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;

    String cid =
        Optional.ofNullable(req.getHeader(headerName))
            .filter(h -> !h.isBlank())
            .orElse("urn:uuid:" + UUID.randomUUID());

    MDC.put(MDC_KEY, cid);
    res.setHeader(headerName, cid);
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
