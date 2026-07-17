package com.vibehopper.infrastructure.web;

import com.vibehopper.application.common.*;
import com.vibehopper.infrastructure.web.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<ApiErrorResponse> notFound(NotFoundException e, HttpServletRequest r) {
    return error(HttpStatus.NOT_FOUND, e.getMessage(), r, List.of());
  }

  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ApiErrorResponse> conflict(ConflictException e, HttpServletRequest r) {
    return error(HttpStatus.CONFLICT, e.getMessage(), r, List.of());
  }

  @ExceptionHandler(ForbiddenException.class)
  ResponseEntity<ApiErrorResponse> forbidden(ForbiddenException e, HttpServletRequest r) {
    return error(HttpStatus.FORBIDDEN, e.getMessage(), r, List.of());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiErrorResponse> badRequest(IllegalArgumentException e, HttpServletRequest r) {
    return error(HttpStatus.BAD_REQUEST, e.getMessage(), r, List.of());
  }

  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<ApiErrorResponse> unauthorized(BadCredentialsException e, HttpServletRequest r) {
    return error(HttpStatus.UNAUTHORIZED, "Invalid email or password", r, List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiErrorResponse> validation(
      MethodArgumentNotValidException e, HttpServletRequest r) {
    List<FieldErrorResponse> fields =
        e.getBindingResult().getFieldErrors().stream()
            .map(f -> new FieldErrorResponse(f.getField(), f.getDefaultMessage()))
            .toList();
    return error(HttpStatus.BAD_REQUEST, "Validation failed", r, fields);
  }

  @ExceptionHandler({ConstraintViolationException.class, HandlerMethodValidationException.class})
  ResponseEntity<ApiErrorResponse> parameterValidation(Exception e, HttpServletRequest r) {
    return error(HttpStatus.BAD_REQUEST, "Validation failed", r, List.of());
  }

  @ExceptionHandler(TypeMismatchException.class)
  ResponseEntity<ApiErrorResponse> invalidParameter(TypeMismatchException e, HttpServletRequest r) {
    String field = e.getPropertyName() == null ? "parameter" : e.getPropertyName();
    return error(
        HttpStatus.BAD_REQUEST,
        "Invalid request parameter",
        r,
        List.of(new FieldErrorResponse(field, "has an invalid value")));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  ResponseEntity<ApiErrorResponse> missingParameter(
      MissingServletRequestParameterException e, HttpServletRequest r) {
    return error(
        HttpStatus.BAD_REQUEST,
        "Missing request parameter",
        r,
        List.of(new FieldErrorResponse(e.getParameterName(), "is required")));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiErrorResponse> malformedBody(
      HttpMessageNotReadableException e, HttpServletRequest r) {
    return error(HttpStatus.BAD_REQUEST, "Malformed request body", r, List.of());
  }

  private ResponseEntity<ApiErrorResponse> error(
      HttpStatus s, String m, HttpServletRequest r, List<FieldErrorResponse> f) {
    return ResponseEntity.status(s)
        .body(
            new ApiErrorResponse(
                Instant.now(), s.value(), s.getReasonPhrase(), m, r.getRequestURI(), f));
  }
}
