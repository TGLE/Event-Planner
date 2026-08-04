package com.tgle.planner.friendship.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Friendship {
    @EqualsAndHashCode.Include
    private final Long id;
    private final Long requesterId;
    private final Long receiverId;
    private final FriendshipStatus status;

    public static Friendship create(
            Long requesterId,
            Long receiverId,
            FriendshipStatus status
    ) {
        return Friendship.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(status)
                .build();
    }
}
