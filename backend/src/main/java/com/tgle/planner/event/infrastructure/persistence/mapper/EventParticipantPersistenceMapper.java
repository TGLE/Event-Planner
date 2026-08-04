package com.tgle.planner.event.infrastructure.persistence.mapper;

import com.tgle.planner.event.domain.model.EventParticipant;
import com.tgle.planner.event.infrastructure.persistence.entity.EventParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventParticipantPersistenceMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "event.id", source = "eventId")
    @Mapping(target = "ticketType.id", source = "ticketTypeId")
    EventParticipantEntity toJpaEntity(EventParticipant eventParticipant);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "ticketTypeId", source = "ticketType.id")
    EventParticipant toDomainEntity(EventParticipantEntity eventParticipantEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "ticketType", ignore = true)
    void updateJpaEntity(EventParticipant eventParticipant,
                         @MappingTarget EventParticipantEntity eventParticipantEntity);

    List<EventParticipantEntity> toJpaEntityList(List<EventParticipant> eventParticipants);

    List<EventParticipant> toDomainEntityList(List<EventParticipantEntity> eventParticipantEntities);
}
