package com.tgle.planner.event.infrastructure.persistence.mapper;

import com.tgle.planner.event.domain.model.TicketType;
import com.tgle.planner.event.infrastructure.persistence.entity.TicketTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketTypePersistenceMapper {

    @Mapping(target = "event.id", source = "eventId")
    TicketTypeEntity toJpaEntity(TicketType ticketType);

    @Mapping(target = "eventId", source = "event.id")
    TicketType toDomainEntity(TicketTypeEntity ticketTypeEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateJpaEntity(TicketType ticketType, @MappingTarget TicketTypeEntity ticketTypeEntity);

    List<TicketTypeEntity> toJpaEntityList(List<TicketType> ticketTypes);

    List<TicketType> toDomainEntityList(List<TicketTypeEntity> ticketTypeEntities);
}
