package com.multi.adapters.infra.errors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static com.multi.adapters.infra.errors.ProblemTypes.*;

@RestControllerAdvice
public class ProblemDetailsHandler {

  private static final Logger log = LoggerFactory.getLogger(ProblemDetailsHandler.class);
  private static final MediaType PROBLEM = MediaType.APPLICATION_PROBLEM_JSON;

  /* ----------------------------- core/problem builder ----------------------------- */

  private static ProblemDetail pd(HttpServletRequest req,
                                  HttpStatus status,
                                  ProblemTypes type,
                                  String title,
                                  String detail) {

    ProblemDetail p = ProblemDetail.forStatus(status);
    p.setType(type.uri());
    p.setTitle(title);
    if (detail != null && !detail.isBlank()) p.setDetail(detail);

    // Correlation id: prefer request attribute -> header -> MDC -> random
    String cid =
      Optional.ofNullable((String) req.getAttribute("X-Correlation-Id"))
        .filter(s -> !s.isBlank())
        .or(() -> Optional.ofNullable(req.getHeader("X-Correlation-Id")))
        .filter(s -> !s.isBlank())
        .or(() -> Optional.ofNullable(MDC.get("correlationId")))
        .filter(s -> !s.isBlank())
        .orElseGet(() -> "urn:uuid:" + UUID.randomUUID());

    // RFC 9457 recommends unique instance per occurrence
    p.setInstance(URI.create("urn:trace:" + cid));

    // RFC 9457 §3.2: extension members
    p.setProperty("correlationId", cid);
    p.setProperty("timestamp", java.time.Clock.systemUTC().instant().toString());
    p.setProperty("method", req.getMethod());
    p.setProperty("path", Optional.ofNullable(req.getRequestURI()).orElse("/"));
    return p;
  }

  /* ----------------------------------- 400s ----------------------------------- */

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> onInvalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
      .map(ProblemDetailsHandler::fieldErr)
      .toList();

    var p = pd(req, HttpStatus.BAD_REQUEST, VALIDATION,
      "Validation failed", "One or more fields are invalid.");
    p.setProperty("errors", errors);

    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ProblemDetail> onHandlerMethodValidation(
    HandlerMethodValidationException ex, HttpServletRequest req) {

    var errors = ex.getParameterValidationResults().stream()
      .flatMap(result -> result.getResolvableErrors().stream().map(msr -> {
        var param = Optional.ofNullable(result.getMethodParameter().getParameterName()).orElse("param");

        if (msr instanceof FieldError fe) {
          return Map.<String, Object>of(
            "parameter", param,
            "field", fe.getField(),
            "path", fe.getField(),
            "message", Optional.ofNullable(fe.getDefaultMessage()).orElse("invalid"),
            "rejectedValue", Optional.ofNullable(fe.getRejectedValue()).orElse("null")
          );
        }

        return Map.<String, Object>of(
          "parameter", param,
          "path", param,
          "message", Optional.ofNullable(msr.getDefaultMessage()).orElse("invalid"),
          "invalidValue", Optional.ofNullable(result.getArgument()).orElse("null")
        );
      }))
      .toList();


    var p = pd(req, HttpStatus.BAD_REQUEST, CONSTRAINT,
      "Constraint violation", "Request violates constraints.");
    p.setProperty("errors", errors);

    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> onConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    var errors = ex.getConstraintViolations().stream()
      .map(ProblemDetailsHandler::violation)
      .toList();

    var p = pd(req, HttpStatus.BAD_REQUEST, CONSTRAINT,
      "Constraint violation", "Request violates constraints.");
    p.setProperty("errors", errors);

    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ProblemDetail> onBadRequest(Exception ex, HttpServletRequest req) {
    var p = pd(req, HttpStatus.BAD_REQUEST, BAD_REQUEST, "Bad request", safeMessage(ex));
    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  /* -------------------------------- 401 / 403 -------------------------------- */

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> onForbidden(AccessDeniedException ex, HttpServletRequest req) {
    var p = pd(req, HttpStatus.FORBIDDEN, FORBIDDEN,
      "Forbidden", "You do not have permission for this resource.");
    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  /* ---------------------------- 404 / 405 / 415 ---------------------------- */

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ProblemDetail> onNotFound(NoHandlerFoundException ex, HttpServletRequest req) {
    var p = pd(req, HttpStatus.NOT_FOUND, NOT_FOUND,
      "Not found", "Endpoint does not exist.");
    // already added method/path via pd(req,…)
    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> onMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
    var p = pd(req, HttpStatus.METHOD_NOT_ALLOWED, METHOD_NOT_ALLOWED,
      "Method not allowed", "HTTP method is not allowed for this endpoint.");

    // FIX: serialize allowed methods as strings (Jackson-safe)
    List<String> allowed = Optional.ofNullable(ex.getSupportedHttpMethods())
      .orElse(Set.of())
      .stream().map(HttpMethod::name).toList();
    p.setProperty("allowed", allowed);

    HttpHeaders headers = new HttpHeaders();
    if (!allowed.isEmpty()) {
      headers.setAllow(allowed.stream().map(HttpMethod::valueOf).collect(Collectors.toSet()));
    }
    headers.setContentType(PROBLEM);

    return new ResponseEntity<>(p, headers, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> onUnsupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
    var p = pd(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, UNSUPPORTED_MEDIA,
      "Unsupported media type", "Content type is not supported for this endpoint.");

    // FIX: serialize supported media types as strings (Jackson-safe)
    List<String> supported = ex.getSupportedMediaTypes()
      .stream()
      .map(MediaType::toString)
      .toList();

    HttpHeaders headers = new HttpHeaders();
    if (!supported.isEmpty()) {
      headers.setAccept(ex.getSupportedMediaTypes());
    }
    headers.setContentType(PROBLEM);
    p.setProperty("supported", supported);

    return ResponseEntity
      .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
      .headers(headers)
      .contentType(PROBLEM)
      .body(p);

  }

  /* ------------------- Spring errors that already have ProblemDetail ------------------- */

  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ProblemDetail> onErrorResponse(
    ErrorResponseException ex, HttpServletRequest req) {

    ProblemDetail body = ex.getBody();

    // If no instance/correlation on the body, rebuild using our pd(...) then merge props.
    if (body.getInstance() == null) {
      var patched = pd(
        req,
        HttpStatus.valueOf(body.getStatus()),
        INTERNAL,
        Optional.ofNullable(body.getTitle()).orElse("Error"),
        Optional.ofNullable(body.getDetail()).orElse("Request failed")
      );
      assert body.getProperties() != null;
      body.getProperties().forEach(patched::setProperty); // merge existing custom props
      body = patched;
    } else {
      // Ensure our RFC 9457 extensions exist (idempotent)
      ensureProp(body, "timestamp", java.time.Clock.systemUTC().instant().toString());
      ensureProp(body, "method", req.getMethod());
      ensureProp(body, "path", Optional.ofNullable(req.getRequestURI()).orElse("/"));
      ensureProp(body, "correlationId",
        Optional.ofNullable((String) req.getAttribute("X-Correlation-Id"))
          .orElse(Optional.ofNullable(req.getHeader("X-Correlation-Id"))
            .orElse(Optional.ofNullable(MDC.get("correlationId")).orElse("unknown"))));
    }

    return ResponseEntity.status(ex.getStatusCode()).contentType(PROBLEM).body(body);
  }

  /** Add property only if it's absent (ProblemDetail has no setPropertyIfAbsent). */
  private static void ensureProp(ProblemDetail p, String key, Object value) {
    var props = Optional.ofNullable(p.getProperties()).orElseGet(Map::of);
    if (!props.containsKey(key)) {
      p.setProperty(key, value);
    }
  }



  /* ----------------------------------- 500 ----------------------------------- */

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> onAny(Exception ex, HttpServletRequest req) {
    log.error("Unhandled exception", ex);
    var p = pd(req, HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL,
      "Internal error", "Unexpected server error");
    return ResponseEntity.status(p.getStatus()).contentType(PROBLEM).body(p);
  }

  /* --------------------------------- helpers --------------------------------- */

  private static Map<String, Object> fieldErr(FieldError e) {
    return Map.of(
      "field", e.getField(),
      "message", Optional.ofNullable(e.getDefaultMessage()).orElse("invalid"),
      "rejectedValue", Optional.ofNullable(e.getRejectedValue()).orElse("null")
    );
  }


  private static Map<String, Object> violation(ConstraintViolation<?> v) {
    return Map.of(
      "path", v.getPropertyPath().toString(),
      "message", Optional.ofNullable(v.getMessage()).orElse("invalid"),
      "invalidValue", v.getInvalidValue()
    );
  }

  private static String safeMessage(Exception ex) {
    String msg = Optional.ofNullable(ex.getMessage()).orElse("Malformed request");
    return msg.length() > 512 ? msg.substring(0, 512) + "…" : msg;
  }
}
