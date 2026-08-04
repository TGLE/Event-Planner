package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.domain.repository.EventRepository;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import com.tgle.planner.event.infrastructure.persistence.mapper.EventPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final EventPersistenceMapper eventMapper;

    @Override
    public Event save(Event event) {
        if (event.getId() == null) {
            EventEntity entity = eventMapper.toJpaEntity(event);
            return eventMapper.toDomainEntity(eventJpaRepository.save(entity));
        }
        EventEntity existingEntity = eventJpaRepository.findById(event.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event"));
        eventMapper.updateJpaEntity(event, existingEntity);
        return eventMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<Event> saveAll(List<Event> events) {
        Set<Long> existingIds = new HashSet<>();

        for (Event event : events) {
            if (event.getId() == null) {
                continue;
            }

            if (!existingIds.add(event.getId())) {
                throw new IllegalArgumentException("Duplicate event id found: " + event.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<EventEntity> entities = eventMapper.toJpaEntityList(events);
            return eventMapper.toDomainEntityList(eventJpaRepository.saveAll(entities));
        }

        Map<Long, EventEntity> existingEntities = eventJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(EventEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Event", "ids", missingIds);
        }

        List<EventEntity> entities = new ArrayList<>(events.size());
        for (Event event : events) {
            if (event.getId() == null) {
                entities.add(eventMapper.toJpaEntity(event));
                continue;
            }
            EventEntity existing = existingEntities.get(event.getId());
            eventMapper.updateJpaEntity(event, existing);
            entities.add(existing);
        }
        return eventMapper.toDomainEntityList(eventJpaRepository.saveAll(entities));
    }
}
