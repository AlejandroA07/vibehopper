package com.almeida.socialsyncapi.infrastructure.exception;

public class ForbiddenResourceException extends RuntimeException {
  public ForbiddenResourceException(String message) {
    super(message);
  }
}
