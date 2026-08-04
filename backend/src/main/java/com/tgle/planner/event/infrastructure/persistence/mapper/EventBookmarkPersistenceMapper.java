package com.tgle.planner.event.infrastructure.persistence.mapper;

import com.tgle.planner.event.domain.model.EventBookmark;
import com.tgle.planner.event.infrastructure.persistence.entity.EventBookmarkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventBookmarkPersistenceMapper {

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "event.id", source = "eventId")
    EventBookmarkEntity toJpaEntity(EventBookmark eventBookmark);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    EventBookmark toDomainEntity(EventBookmarkEntity eventBookmarkEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "event", ignore = true)
    void updateJpaEntity(EventBookmark eventBookmark, @MappingTarget EventBookmarkEntity eventBookmarkEntity);

    List<EventBookmarkEntity> toJpaEntityList(List<EventBookmark> eventBookmarks);

    List<EventBookmark> toDomainEntityList(List<EventBookmarkEntity> eventBookmarkEntities);
}
