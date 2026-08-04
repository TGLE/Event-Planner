package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.domain.model.EventBookmark;
import com.tgle.planner.event.domain.repository.EventBookmarkRepository;
import com.tgle.planner.event.infrastructure.persistence.entity.EventBookmarkEntity;
import com.tgle.planner.event.infrastructure.persistence.mapper.EventBookmarkPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventBookmarkRepositoryImpl implements EventBookmarkRepository {

    private final EventBookmarkJpaRepository eventBookmarkJpaRepository;
    private final EventBookmarkPersistenceMapper eventBookmarkMapper;

    @Override
    public EventBookmark save(EventBookmark eventBookmark) {
        if (eventBookmark.getId() == null) {
            EventBookmarkEntity entity = eventBookmarkMapper.toJpaEntity(eventBookmark);
            return eventBookmarkMapper.toDomainEntity(eventBookmarkJpaRepository.save(entity));
        }
        EventBookmarkEntity existingEntity = eventBookmarkJpaRepository.findById(eventBookmark.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event bookmark"));
        eventBookmarkMapper.updateJpaEntity(eventBookmark, existingEntity);
        return eventBookmarkMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<EventBookmark> saveAll(List<EventBookmark> eventBookmarks) {
        Set<Long> existingIds = new HashSet<>();

        for (EventBookmark eventBookmark : eventBookmarks) {
            if (eventBookmark.getId() == null) {
                continue;
            }

            if (!existingIds.add(eventBookmark.getId())) {
                throw new IllegalArgumentException("Duplicate event bookmark id found: " + eventBookmark.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<EventBookmarkEntity> entities = eventBookmarkMapper.toJpaEntityList(eventBookmarks);
            return eventBookmarkMapper.toDomainEntityList(eventBookmarkJpaRepository.saveAll(entities));
        }

        Map<Long, EventBookmarkEntity> existingEntities = eventBookmarkJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(EventBookmarkEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Event bookmark", "ids", missingIds);
        }

        List<EventBookmarkEntity> entities = new ArrayList<>(eventBookmarks.size());
        for (EventBookmark eventBookmark : eventBookmarks) {
            if (eventBookmark.getId() == null) {
                entities.add(eventBookmarkMapper.toJpaEntity(eventBookmark));
                continue;
            }
            EventBookmarkEntity existing = existingEntities.get(eventBookmark.getId());
            eventBookmarkMapper.updateJpaEntity(eventBookmark, existing);
            entities.add(existing);
        }
        return eventBookmarkMapper.toDomainEntityList(eventBookmarkJpaRepository.saveAll(entities));
    }
}
