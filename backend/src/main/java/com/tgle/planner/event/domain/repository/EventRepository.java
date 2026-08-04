package com.tgle.planner.event.domain.repository;

import com.tgle.planner.event.domain.model.Event;

import java.util.List;

public interface EventRepository {
    Event save(Event event);
    List<Event> saveAll(List<Event> events);
}
