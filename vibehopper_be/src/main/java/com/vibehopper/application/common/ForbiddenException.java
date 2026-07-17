package com.vibehopper.application.common;

public final class ForbiddenException extends RuntimeException {
  public ForbiddenException(String message) {
    super(message);
  }
}
