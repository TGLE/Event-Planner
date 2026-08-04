package com.tgle.planner.notification.infrastructure.persistence;

import com.tgle.planner.notification.domain.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationPersistenceMapper {

    @Mapping(target = "recipient.id", source = "recipientId")
    NotificationEntity toJpaEntity(Notification notification);

    @Mapping(target = "recipientId", source = "recipient.id")
    Notification toDomainEntity(NotificationEntity notificationEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    void updateJpaEntity(Notification notification, @MappingTarget NotificationEntity notificationEntity);

    List<NotificationEntity> toJpaEntityList(List<Notification> notifications);

    List<Notification> toDomainEntityList(List<NotificationEntity> notificationEntities);
}
