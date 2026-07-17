package com.vibehopper.application.event.query;

import com.vibehopper.domain.event.Event;
import java.util.List;

public record EventPage(List<Event> items, int page, int size, long totalItems) {
  public EventPage {
    items = List.copyOf(items);
  }

  public int totalPages() {
    return totalItems == 0 ? 0 : (int) ((totalItems + size - 1) / size);
  }
}
