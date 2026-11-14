package com.tgle.planner.event.domain.repository;

import com.tgle.planner.event.domain.model.EventInvitation;

import java.util.List;

public interface EventInvitationRepository {
    EventInvitation save(EventInvitation eventInvitation);
    List<EventInvitation> saveAll(List<EventInvitation> eventInvitations);
}
