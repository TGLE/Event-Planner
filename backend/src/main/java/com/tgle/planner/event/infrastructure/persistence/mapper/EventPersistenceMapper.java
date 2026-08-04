package com.tgle.planner.event.infrastructure.persistence.mapper;

import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventPersistenceMapper {

    @Mapping(target = "organizer.id", source = "organizerId")
    EventEntity toJpaEntity(Event event);

    @Mapping(target = "organizerId", source = "organizer.id")
    Event toDomainEntity(EventEntity event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    void updateJpaEntity(Event event, @MappingTarget EventEntity eventEntity);

    List<EventEntity> toJpaEntityList(List<Event> events);

    List<Event> toDomainEntityList(List<EventEntity> eventEntities);
}
