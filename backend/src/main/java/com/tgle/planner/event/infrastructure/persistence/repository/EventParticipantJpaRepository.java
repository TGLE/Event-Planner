package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.event.infrastructure.persistence.entity.EventParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipantJpaRepository extends JpaRepository<EventParticipantEntity, Long> {
}
