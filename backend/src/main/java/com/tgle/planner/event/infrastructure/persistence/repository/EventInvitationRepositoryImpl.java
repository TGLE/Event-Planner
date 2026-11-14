package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.domain.model.EventInvitation;
import com.tgle.planner.event.domain.repository.EventInvitationRepository;
import com.tgle.planner.event.infrastructure.persistence.entity.EventInvitationEntity;
import com.tgle.planner.event.infrastructure.persistence.mapper.EventInvitationPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EventInvitationRepositoryImpl implements EventInvitationRepository {

    private final EventInvitationJpaRepository eventInvitationJpaRepository;
    private final EventInvitationPersistenceMapper eventInvitationMapper;

    @Override
    public EventInvitation save(EventInvitation eventInvitation) {
        if (eventInvitation.getId() == null) {
            EventInvitationEntity entity = eventInvitationMapper.toJpaEntity(eventInvitation);
            return eventInvitationMapper.toDomainEntity(eventInvitationJpaRepository.save(entity));
        }
        EventInvitationEntity existingEntity = eventInvitationJpaRepository.findById(eventInvitation.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Event invitation"));
        eventInvitationMapper.updateJpaEntity(eventInvitation, existingEntity);
        return eventInvitationMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<EventInvitation> saveAll(List<EventInvitation> eventInvitations) {
        Set<Long> existingIds = new HashSet<>();

        for (EventInvitation eventInvitation : eventInvitations) {
            if (eventInvitation.getId() == null) {
                continue;
            }

            if (!existingIds.add(eventInvitation.getId())) {
                throw new IllegalArgumentException("Duplicate event invitation id found: " + eventInvitation.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<EventInvitationEntity> entities = eventInvitationMapper.toJpaEntityList(eventInvitations);
            return eventInvitationMapper.toDomainEntityList(eventInvitationJpaRepository.saveAll(entities));
        }

        Map<Long, EventInvitationEntity> existingEntities = eventInvitationJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(EventInvitationEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Event invitation", "ids", missingIds);
        }

        List<EventInvitationEntity> entities = new ArrayList<>(eventInvitations.size());
        for (EventInvitation eventInvitation : eventInvitations) {
            if (eventInvitation.getId() == null) {
                entities.add(eventInvitationMapper.toJpaEntity(eventInvitation));
                continue;
            }
            EventInvitationEntity existing = existingEntities.get(eventInvitation.getId());
            eventInvitationMapper.updateJpaEntity(eventInvitation, existing);
            entities.add(existing);
        }
        return eventInvitationMapper.toDomainEntityList(eventInvitationJpaRepository.saveAll(entities));
    }
}
