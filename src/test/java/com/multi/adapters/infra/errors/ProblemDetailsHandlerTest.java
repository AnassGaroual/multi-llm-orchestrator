package com.multi.adapters.infra.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end tests for RFC 9457 Problem Details using MockMvc.
 * <p>
 * Requires application.properties:
 *   spring.mvc.throw-exception-if-no-handler-found=true
 *   spring.web.resources.add-mappings=false
 *   app.http.correlation-header=X-Correlation-Id
 */
@SpringBootTest(
  classes = ProblemDetailsHandlerTest.TestApp.class,
  properties = {
    "spring.mvc.throw-exception-if-no-handler-found=true",
    "spring.web.resources.add-mappings=false",
    "app.http.correlation-header=X-Correlation-Id",

    // disable JDBC
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
      "org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration,"
  }
)
@AutoConfigureMockMvc
@Import({ ProblemDetailsHandler.class, com.multi.adapters.infra.http.CorrelationIdFilter.class,
  ProblemDetailsHandlerTest.TestEndpoints.class})
class ProblemDetailsHandlerTest {

  @Autowired MockMvc mvc;

  // ---------- Happy path helper ----------
  private static String json() { return """
      { "name": "  " }
    """; }

  // ---------- 400: Bean validation payload ----------
  @Test
  @DisplayName("400 validation: body fails @NotBlank → RFC9457 problem with errors[] and correlation")
  void validation_error_returns_problem_details() throws Exception {
    var body = json();

    mvc.perform(post("/t/echo")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest())
      .andExpect(header().exists("X-Correlation-Id"))
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.type").value(Matchers.startsWith("urn:problem:")))
      .andExpect(jsonPath("$.title").value("Validation failed"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.instance").exists())
      .andExpect(jsonPath("$.correlationId").exists())
      .andExpect(jsonPath("$.timestamp").exists())
      .andExpect(jsonPath("$.errors[0].field").value("name"));
  }

  // ---------- 400: Constraint violation (query param) ----------
  @Test
  @DisplayName("400 constraint: @Min on query param → RFC9457 problem with errors[]")
  void constraint_violation_returns_problem_details() throws Exception {
    mvc.perform(get("/t/age-check").param("age", "-1"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.type").value("urn:problem:constraint-violation"))
      .andExpect(jsonPath("$.errors[0].path").value("age"));
  }

  // ---------- 404 ----------
  @Test
  @DisplayName("404 not found: unroutable path → RFC9457 problem with path/method")
  void not_found_returns_problem_details() throws Exception {
    mvc.perform(get("/this/does/not/exist"))
      .andExpect(status().isNotFound())
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.type").value("urn:problem:not-found"))
      .andExpect(jsonPath("$.title").value("Not found"))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.path").exists())
      .andExpect(jsonPath("$.method").value("GET"));
  }

  // ---------- 405 ----------
  @Test
  @DisplayName("405 method not allowed → RFC9457 problem with allowed methods")
  void method_not_allowed_returns_problem_details() throws Exception {
    mvc.perform(post("/t/only-get"))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.type").value("urn:problem:method-not-allowed"))
      .andExpect(jsonPath("$.allowed").isArray());
  }

  // ---------- 415 ----------
  @Test
  @DisplayName("415 unsupported media type → RFC9457 problem with supported types")
  void unsupported_media_type_returns_problem_details() throws Exception {
    mvc.perform(post("/t/echo")
        .contentType(MediaType.TEXT_PLAIN)
        .content("name=foo"))
      .andExpect(status().isUnsupportedMediaType())
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.type").value("urn:problem:unsupported-media-type"))
      .andExpect(jsonPath("$.supported").isArray());
  }

  // ---------- 500 ----------
  @Test
  @DisplayName("500 internal error: runtime exception → RFC9457 problem without leakage")
  void internal_error_returns_problem_details() throws Exception {
    mvc.perform(get("/t/bomb"))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType("application/problem+json"))
      .andExpect(jsonPath("$.type").value("urn:problem:internal"))
      .andExpect(jsonPath("$.title").value("Internal error"))
      .andExpect(jsonPath("$.detail").value("Unexpected server error"))
      .andExpect(jsonPath("$.instance").exists())
      .andExpect(jsonPath("$.correlationId").exists());
  }

  // ====== Test-only minimal application & controller ======
  @SpringBootApplication(scanBasePackages = "com.multi")
  static class TestApp { }

  @RestController
  @RequestMapping("/t")
  @Validated
  static class TestController {

    record EchoReq(@NotBlank String name) {
      @JsonCreator EchoReq(@JsonProperty("name") String name) { this.name = name; }
    }

    @PostMapping(path = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EchoReq echo(@RequestBody @Valid EchoReq req) {
      return req;
    }

    @GetMapping("/age-check")
    public String ageCheck(@RequestParam @Min(0) int age) {
      return "ok:" + age;
    }

    @GetMapping("/only-get")
    public String onlyGet() { return "GET"; }

    @GetMapping("/bomb")
    public String bomb() {
      throw new RuntimeException("boom (should not leak)");
    }
  }

  @RestController
  @RequestMapping("/t")
  static class TestEndpoints {

    // Exists for GET only. Hitting POST /t/only-get → 405
    @GetMapping("/only-get")
    public Map<String, String> onlyGet() {
      return Map.of("ok", "1");
    }

    // Validates JSON body. Blank name → 400 (MethodArgumentNotValidException)
    @PostMapping(path = "/echo", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> echo(@Valid @RequestBody EchoReq body) {
      return Map.of("name", body.name());
    }

    // Text/plain to /t/echo will now be 415, because consumes = application/json
    // (no extra code needed)

    // Force a 500 to test internal error handling
    @GetMapping("/bomb")
    public void bomb() {
      throw new RuntimeException("boom");
    }

    // Constraint violation on query param → 400 (ConstraintViolationException)
    @GetMapping("/age-check")
    public void ageCheck(@RequestParam @Min(0) int age) { /* no-op */ }

    // DTO with Bean Validation
    static record EchoReq(@NotBlank String name) {}
  }
}

