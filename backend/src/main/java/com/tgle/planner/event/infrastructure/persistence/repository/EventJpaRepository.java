package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    Optional<EventEntity> findByPublicId(Long publicId);
}
