package com.vibehopper.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
public abstract class PostgresContainerSupport {
  private static final DockerImageName POSTGIS_IMAGE =
      DockerImageName.parse("postgis/postgis:16-3.4").asCompatibleSubstituteFor("postgres");

  @Container
  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>(POSTGIS_IMAGE)
          .withDatabaseName("vibehopperdb")
          .withUsername("vibehopper")
          .withPassword("vibehopper");

  @DynamicPropertySource
  static void configurePostgres(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    registry.add("spring.flyway.enabled", () -> true);
    registry.add("spring.flyway.locations", () -> "classpath:db/migration");
  }
}
