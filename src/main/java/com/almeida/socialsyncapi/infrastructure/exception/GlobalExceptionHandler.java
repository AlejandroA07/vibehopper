package com.almeida.socialsyncapi.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    Map<String, String> fields = new LinkedHashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

    return ResponseEntity.badRequest()
        .body(ApiError.validation("Request validation failed", request.getRequestURI(), fields));
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ApiError> handleDuplicate(
      DuplicateResourceException exception, HttpServletRequest request) {
    return error(HttpStatus.CONFLICT, exception.getMessage(), request);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(
      ResourceNotFoundException exception, HttpServletRequest request) {
    return error(HttpStatus.NOT_FOUND, exception.getMessage(), request);
  }

  @ExceptionHandler({ForbiddenResourceException.class, AccessDeniedException.class})
  public ResponseEntity<ApiError> handleForbidden(
      RuntimeException exception, HttpServletRequest request) {
    return error(HttpStatus.FORBIDDEN, "Forbidden", request);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> handleBadCredentials(
      BadCredentialsException exception, HttpServletRequest request) {
    return error(HttpStatus.UNAUTHORIZED, "Invalid credentials", request);
  }

  private ResponseEntity<ApiError> error(
      HttpStatus status, String message, HttpServletRequest request) {
    return ResponseEntity.status(status)
        .body(
            ApiError.of(
                status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
  }
}
