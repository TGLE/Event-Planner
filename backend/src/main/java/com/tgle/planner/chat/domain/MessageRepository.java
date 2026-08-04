package com.tgle.planner.chat.domain;

import java.util.List;

public interface MessageRepository {

    Message save(Message message);
    List<Message> saveAll(List<Message> messages);
}
