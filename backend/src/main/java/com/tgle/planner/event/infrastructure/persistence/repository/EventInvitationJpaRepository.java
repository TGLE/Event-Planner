package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.event.infrastructure.persistence.entity.EventInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventInvitationJpaRepository extends JpaRepository<EventInvitationEntity, Long> {
}
