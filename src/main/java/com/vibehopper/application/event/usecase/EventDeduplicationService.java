package com.vibehopper.application.event.usecase;
import com.vibehopper.application.event.port.EventRepositoryPort; import com.vibehopper.domain.event.Event;
import java.text.Normalizer; import java.util.Locale;
public final class EventDeduplicationService {
    private final EventRepositoryPort eventRepository;
    public EventDeduplicationService(EventRepositoryPort eventRepository) { this.eventRepository = eventRepository; }
    public boolean isDuplicate(Event event) {
        if (event.source()!=null && hasText(event.source().sourceName()) && hasText(event.source().externalId())) return eventRepository.existsBySource(event.source().sourceName(), event.source().externalId());
        return eventRepository.existsByNormalizedIdentity(normalize(event.name()), event.date(), normalize(event.city()));
    }
    public String normalize(String value) {
        if (value == null) return null;
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }
    private boolean hasText(String value) { return value != null && !value.isBlank(); }
}
