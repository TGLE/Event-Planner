package com.tgle.planner.chat.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Message {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long senderId;
    private final Long recipientId;
    private final String content;
    private final Instant sentAt;
    private final Instant editedAt;

    public static Message create(
            Long senderId,
            Long recipientId,
            String content,
            Instant sentAt
    ) {
        return Message.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .content(content)
                .sentAt(sentAt)
                .build();
    }
}
