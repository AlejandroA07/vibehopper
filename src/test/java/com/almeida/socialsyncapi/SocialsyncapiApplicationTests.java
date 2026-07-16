package com.almeida.socialsyncapi;

import com.almeida.socialsyncapi.repository.AppUserRepository;
import com.almeida.socialsyncapi.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration"
    })
class SocialsyncapiApplicationTests {

  @MockBean private AppUserRepository appUserRepository;

  @MockBean private EventRepository eventRepository;

  @DynamicPropertySource
  static void jwtProperties(DynamicPropertyRegistry registry) {
    registry.add("security.jwt.secret", () -> TestJwtSecret.VALUE);
  }

  @Test
  void contextLoads() {}
}
