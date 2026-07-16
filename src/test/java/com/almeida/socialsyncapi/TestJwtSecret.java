package com.almeida.socialsyncapi;

import java.util.Base64;

final class TestJwtSecret {
  static final String VALUE = Base64.getEncoder().encodeToString(new byte[32]);

  private TestJwtSecret() {}
}
