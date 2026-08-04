package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.event.infrastructure.persistence.entity.EventBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventBookmarkJpaRepository extends JpaRepository<EventBookmarkEntity, Long> {
}
