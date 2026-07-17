package com.vibehopper.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Security")
public class CsrfController {
  @GetMapping("/api/csrf")
  @Operation(
      operationId = "getCsrfToken",
      summary = "Issue a CSRF token",
      description = "Returns the header name and token while issuing the matching HttpOnly cookie.")
  public CsrfTokenResponse csrf(CsrfToken csrfToken) {
    return new CsrfTokenResponse(csrfToken.getHeaderName(), csrfToken.getToken());
  }

  public record CsrfTokenResponse(String headerName, String token) {}
}
