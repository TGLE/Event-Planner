package com.tgle.planner.event.infrastructure.persistence.mapper;

import com.tgle.planner.event.application.CapacityMapper;
import com.tgle.planner.event.domain.model.EventInvitation;
import com.tgle.planner.event.infrastructure.persistence.entity.EventInvitationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CapacityMapper.class)
public interface EventInvitationPersistenceMapper {

    @Mapping(target = "invitee.id", source = "inviteeId")
    @Mapping(target = "event.id", source = "eventId")
    EventInvitationEntity toJpaEntity(EventInvitation eventInvitation);

    @Mapping(target = "inviteeId", source = "invitee.id")
    @Mapping(target = "eventId", source = "event.id")
    EventInvitation toDomainEntity(EventInvitationEntity eventInvitationEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invitee", ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateJpaEntity(EventInvitation eventInvitation, @MappingTarget EventInvitationEntity eventInvitationEntity);

    List<EventInvitationEntity> toJpaEntityList(List<EventInvitation> eventInvitations);

    List<EventInvitation> toDomainEntityList(List<EventInvitationEntity> eventInvitationEntities);
}
