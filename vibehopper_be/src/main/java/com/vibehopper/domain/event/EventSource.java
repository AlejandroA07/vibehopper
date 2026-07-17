package com.vibehopper.domain.event;

import java.time.Instant;

public record EventSource(
    SourceType sourceType,
    String sourceName,
    String externalId,
    String sourceUrl,
    Instant lastSyncedAt) {
  public static EventSource manual() {
    return new EventSource(SourceType.MANUAL, "manual", null, null, null);
  }
}
