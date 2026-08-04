package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.event.infrastructure.persistence.entity.TicketTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTypeJpaRepository extends JpaRepository<TicketTypeEntity, Long> {
}
