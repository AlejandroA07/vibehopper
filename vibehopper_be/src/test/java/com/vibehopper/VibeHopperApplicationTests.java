package com.vibehopper;

import com.vibehopper.support.PostgresContainerSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VibeHopperApplicationTests extends PostgresContainerSupport {
  @Test
  void contextLoadsAgainstFlywayManagedPostgresSchema() {}
}
