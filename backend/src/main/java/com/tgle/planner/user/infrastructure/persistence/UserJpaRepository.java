package com.tgle.planner.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"roles", "cooldowns"})
    Optional<UserEntity> findById(Long id);

    @EntityGraph(attributePaths = {"roles", "cooldowns"})
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
