package com.tgle.planner.event.infrastructure.persistence.mapper;

import com.tgle.planner.event.application.CapacityMapper;
import com.tgle.planner.event.domain.model.Event;
import com.tgle.planner.event.domain.model.EventDates;
import com.tgle.planner.event.infrastructure.persistence.entity.EventDatesEmbeddable;
import com.tgle.planner.event.infrastructure.persistence.entity.EventEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CapacityMapper.class)
public interface EventPersistenceMapper {

    @Mapping(target = "organizer.id", source = "organizerId")
    EventEntity toJpaEntity(Event event);

    @Mapping(target = "organizerId", source = "organizer.id")
    Event toDomainEntity(EventEntity eventEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    void updateJpaEntity(Event event, @MappingTarget EventEntity eventEntity);

    List<EventEntity> toJpaEntityList(List<Event> events);

    List<Event> toDomainEntityList(List<EventEntity> eventEntities);

    EventDatesEmbeddable toJpaEntity(EventDates eventDates);

    @BeanMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    EventDates toDomainEntity(EventDatesEmbeddable eventDatesEmbeddable);
}
