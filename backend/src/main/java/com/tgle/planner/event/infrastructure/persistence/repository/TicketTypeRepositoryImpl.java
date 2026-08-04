package com.tgle.planner.event.infrastructure.persistence.repository;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.event.domain.model.TicketType;
import com.tgle.planner.event.domain.repository.TicketTypeRepository;
import com.tgle.planner.event.infrastructure.persistence.entity.TicketTypeEntity;
import com.tgle.planner.event.infrastructure.persistence.mapper.TicketTypePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TicketTypeRepositoryImpl implements TicketTypeRepository {

    private final TicketTypeJpaRepository ticketTypeJpaRepository;
    private final TicketTypePersistenceMapper ticketTypeMapper;

    @Override
    public TicketType save(TicketType ticketType) {
        if (ticketType.getId() == null) {
            TicketTypeEntity entity = ticketTypeMapper.toJpaEntity(ticketType);
            return ticketTypeMapper.toDomainEntity(ticketTypeJpaRepository.save(entity));
        }
        TicketTypeEntity existingEntity = ticketTypeJpaRepository.findById(ticketType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type"));
        ticketTypeMapper.updateJpaEntity(ticketType, existingEntity);
        return ticketTypeMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<TicketType> saveAll(List<TicketType> ticketTypes) {
        Set<Long> existingIds = new HashSet<>();

        for (TicketType ticketType : ticketTypes) {
            if (ticketType.getId() == null) {
                continue;
            }

            if (!existingIds.add(ticketType.getId())) {
                throw new IllegalArgumentException("Duplicate ticket type id found: " + ticketType.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<TicketTypeEntity> entities = ticketTypeMapper.toJpaEntityList(ticketTypes);
            return ticketTypeMapper.toDomainEntityList(ticketTypeJpaRepository.saveAll(entities));
        }

        Map<Long, TicketTypeEntity> existingEntities = ticketTypeJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(TicketTypeEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Ticket type", "ids", missingIds);
        }

        List<TicketTypeEntity> entities = new ArrayList<>(ticketTypes.size());
        for (TicketType ticketType : ticketTypes) {
            if (ticketType.getId() == null) {
                entities.add(ticketTypeMapper.toJpaEntity(ticketType));
                continue;
            }
            TicketTypeEntity existing = existingEntities.get(ticketType.getId());
            ticketTypeMapper.updateJpaEntity(ticketType, existing);
            entities.add(existing);
        }
        return ticketTypeMapper.toDomainEntityList(ticketTypeJpaRepository.saveAll(entities));
    }
}
