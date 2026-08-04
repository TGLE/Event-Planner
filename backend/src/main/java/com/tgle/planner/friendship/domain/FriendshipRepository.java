package com.tgle.planner.friendship.domain;

import java.util.List;

public interface FriendshipRepository {

    Friendship save(Friendship friendship);

    List<Friendship> saveAll(List<Friendship> friendships);
}
