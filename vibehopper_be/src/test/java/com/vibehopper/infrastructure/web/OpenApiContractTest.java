package com.vibehopper.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vibehopper.support.PostgresContainerSupport;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiContractTest extends PostgresContainerSupport {
  private static final Path SNAPSHOT = Path.of("openapi", "vibehopper-api.json");

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void openApiContractIsSafeAndMatchesTheCommittedSnapshot() throws Exception {
    String response =
        mockMvc
            .perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    JsonNode contract = objectMapper.readTree(response);

    assertThat(contract.at("/paths/~1api~1events/get/operationId").asText())
        .isEqualTo("listEvents");
    assertThat(contract.at("/paths/~1api~1events~1search").isMissingNode()).isTrue();
    assertThat(contract.at("/paths/~1api~1events/get/security").isMissingNode()).isTrue();
    assertThat(contract.at("/paths/~1api~1events/post/security/0/bearerAuth").isArray()).isTrue();

    JsonNode createProperties = contract.at("/components/schemas/CreateEventRequest/properties");
    assertThat(createProperties.has("ownerId")).isFalse();
    assertThat(createProperties.has("attendees")).isFalse();
    assertThat(createProperties.has("source")).isFalse();

    JsonNode detailProperties = contract.at("/components/schemas/EventDetailResponse/properties");
    assertThat(detailProperties.has("ownerId")).isFalse();
    assertThat(detailProperties.has("attendees")).isFalse();
    assertThat(detailProperties.has("externalId")).isFalse();
    assertThat(detailProperties.has("lastSyncedAt")).isFalse();

    String canonical =
        objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sort(contract))
            + System.lineSeparator();
    if (Boolean.getBoolean("vibehopper.openapi.update")) {
      Files.createDirectories(SNAPSHOT.getParent());
      Files.writeString(SNAPSHOT, canonical);
      return;
    }

    assertThat(SNAPSHOT)
        .as("Run ./gradlew exportOpenApi after an intentional API contract change")
        .exists();
    assertThat(canonical)
        .as("OpenAPI contract drift; run ./gradlew exportOpenApi and review the snapshot diff")
        .isEqualTo(Files.readString(SNAPSHOT));
  }

  private JsonNode sort(JsonNode node) {
    if (node.isObject()) {
      ObjectNode sorted = objectMapper.createObjectNode();
      Map<String, JsonNode> fields = new TreeMap<>();
      node.properties().forEach(entry -> fields.put(entry.getKey(), entry.getValue()));
      fields.forEach((name, value) -> sorted.set(name, sort(value)));
      return sorted;
    }
    if (node.isArray()) {
      ArrayNode sorted = objectMapper.createArrayNode();
      node.forEach(value -> sorted.add(sort(value)));
      return sorted;
    }
    return node;
  }
}
