package com.tgle.planner.chat.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {

}
