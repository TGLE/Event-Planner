package com.tgle.planner.notification.infrastructure.persistence;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.notification.domain.Notification;
import com.tgle.planner.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationPersistenceMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            NotificationEntity entity = notificationMapper.toJpaEntity(notification);
            return notificationMapper.toDomainEntity(notificationJpaRepository.save(entity));
        }
        NotificationEntity existingEntity = notificationJpaRepository.findById(notification.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification"));
        notificationMapper.updateJpaEntity(notification, existingEntity);
        return notificationMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        Set<Long> existingIds = new HashSet<>();

        for (Notification notification : notifications) {
            if (notification.getId() == null) {
                continue;
            }

            if (!existingIds.add(notification.getId())) {
                throw new IllegalArgumentException("Duplicate notification id found: " + notification.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<NotificationEntity> entities = notificationMapper.toJpaEntityList(notifications);
            return notificationMapper.toDomainEntityList(notificationJpaRepository.saveAll(entities));
        }

        Map<Long, NotificationEntity> existingEntities = notificationJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(NotificationEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Notification", "ids", missingIds);
        }

        List<NotificationEntity> entities = new ArrayList<>(notifications.size());
        for (Notification notification : notifications) {
            if (notification.getId() == null) {
                entities.add(notificationMapper.toJpaEntity(notification));
                continue;
            }
            NotificationEntity existing = existingEntities.get(notification.getId());
            notificationMapper.updateJpaEntity(notification, existing);
            entities.add(existing);
        }
        return notificationMapper.toDomainEntityList(notificationJpaRepository.saveAll(entities));
    }
}
