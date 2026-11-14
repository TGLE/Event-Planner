package com.tgle.planner.event.domain.repository;

import com.tgle.planner.event.domain.model.EventParticipant;

import java.util.List;

public interface EventParticipantRepository {
    long countByEventId(Long eventId);
    EventParticipant save(EventParticipant eventParticipant);
    List<EventParticipant> saveAll(List<EventParticipant> eventParticipants);
}
