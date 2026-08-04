package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.domain.model.EventParticipant;
import com.tgle.planner.event.domain.repository.EventParticipantRepository;
import com.tgle.planner.event.infrastructure.persistence.entity.EventParticipantEntity;
import com.tgle.planner.event.infrastructure.persistence.mapper.EventParticipantPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventParticipantRepositoryImpl implements EventParticipantRepository {

    private final EventParticipantJpaRepository eventParticipantJpaRepository;
    private final EventParticipantPersistenceMapper eventParticipantMapper;

    @Override
    public EventParticipant save(EventParticipant eventParticipant) {
        if (eventParticipant.getId() == null) {
            EventParticipantEntity entity = eventParticipantMapper.toJpaEntity(eventParticipant);
            return eventParticipantMapper.toDomainEntity(eventParticipantJpaRepository.save(entity));
        }
        EventParticipantEntity existingEntity = eventParticipantJpaRepository.findById(eventParticipant.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event participant"));
        eventParticipantMapper.updateJpaEntity(eventParticipant, existingEntity);
        return eventParticipantMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<EventParticipant> saveAll(List<EventParticipant> eventParticipants) {
        Set<Long> existingIds = new HashSet<>();

        for (EventParticipant eventParticipant : eventParticipants) {
            if (eventParticipant.getId() == null) {
                continue;
            }

            if (!existingIds.add(eventParticipant.getId())) {
                throw new IllegalArgumentException("Duplicate event participant id found: " + eventParticipant.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<EventParticipantEntity> entities = eventParticipantMapper.toJpaEntityList(eventParticipants);
            return eventParticipantMapper.toDomainEntityList(eventParticipantJpaRepository.saveAll(entities));
        }

        Map<Long, EventParticipantEntity> existingEntities = eventParticipantJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(EventParticipantEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Event participant", "ids", missingIds);
        }

        List<EventParticipantEntity> entities = new ArrayList<>(eventParticipants.size());
        for (EventParticipant eventParticipant : eventParticipants) {
            if (eventParticipant.getId() == null) {
                entities.add(eventParticipantMapper.toJpaEntity(eventParticipant));
                continue;
            }
            EventParticipantEntity existing = existingEntities.get(eventParticipant.getId());
            eventParticipantMapper.updateJpaEntity(eventParticipant, existing);
            entities.add(existing);
        }
        return eventParticipantMapper.toDomainEntityList(eventParticipantJpaRepository.saveAll(entities));
    }
}
