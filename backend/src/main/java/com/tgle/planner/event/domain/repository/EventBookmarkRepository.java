package com.tgle.planner.event.domain.repository;

import com.tgle.planner.event.domain.model.EventBookmark;

import java.util.List;

public interface EventBookmarkRepository {
    EventBookmark save(EventBookmark eventBookmark);
    List<EventBookmark> saveAll(List<EventBookmark> eventBookmarks);
}
