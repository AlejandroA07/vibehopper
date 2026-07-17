package com.vibehopper.architecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class StaticArchitectureTest {
  private static final Path MAIN_JAVA = Path.of("src/main/java");

  @Test
  void legacyNamesAreGoneFromMainSources() throws IOException {
    List<String> forbidden =
        List.of(
            "social" + "sync", "Social" + "Sync", "social" + "syncapi", "com." + "alm" + "eida");

    List<Path> offenders =
        javaFiles(MAIN_JAVA).stream().filter(path -> containsAny(path, forbidden)).toList();

    assertThat(offenders).isEmpty();
  }

  @Test
  void domainDoesNotDependOnFrameworksOrInfrastructure() throws IOException {
    List<String> forbidden =
        List.of("org.springframework", "jakarta.", "io.jsonwebtoken", "mongodb", "UserDetails");

    List<Path> offenders =
        javaFiles(MAIN_JAVA.resolve("com/vibehopper/domain")).stream()
            .filter(path -> containsAny(path, forbidden))
            .toList();

    assertThat(offenders).isEmpty();
  }

  private static List<Path> javaFiles(Path root) throws IOException {
    try (var stream = Files.walk(root)) {
      return stream.filter(path -> path.toString().endsWith(".java")).toList();
    }
  }

  private static boolean containsAny(Path path, List<String> needles) {
    try {
      String content = Files.readString(path);
      return needles.stream().anyMatch(content::contains);
    } catch (IOException exception) {
      throw new IllegalStateException(exception);
    }
  }
}
