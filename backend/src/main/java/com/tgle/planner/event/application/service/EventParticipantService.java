package com.tgle.planner.event.application.service;

import com.tgle.planner.event.domain.repository.EventParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventParticipantService {

    private final EventParticipantRepository eventParticipantRepository;

    public long countByEventId(Long eventId) {
        return eventParticipantRepository.countByEventId(eventId);
    }
}
