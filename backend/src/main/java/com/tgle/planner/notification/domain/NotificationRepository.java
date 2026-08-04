package com.tgle.planner.notification.domain;

import java.util.List;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> saveAll(List<Notification> notifications);
}
