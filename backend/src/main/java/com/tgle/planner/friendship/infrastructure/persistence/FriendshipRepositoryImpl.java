package com.tgle.planner.friendship.infrastructure.persistence;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.friendship.domain.Friendship;
import com.tgle.planner.friendship.domain.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FriendshipRepositoryImpl implements FriendshipRepository {

    private final FriendshipJpaRepository friendshipJpaRepository;
    private final FriendshipPersistenceMapper friendshipMapper;

    @Override
    public Friendship save(Friendship friendship) {
        if (friendship.getId() == null) {
            FriendshipEntity entity = friendshipMapper.toJpaEntity(friendship);
            return friendshipMapper.toDomainEntity(friendshipJpaRepository.save(entity));
        }
        FriendshipEntity existingEntity = friendshipJpaRepository.findById(friendship.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Friendship"));
        friendshipMapper.updateJpaEntity(friendship, existingEntity);
        return friendshipMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<Friendship> saveAll(List<Friendship> friendships) {
        Set<Long> existingIds = new HashSet<>();

        for (Friendship friendship : friendships) {
            if (friendship.getId() == null) {
                continue;
            }

            if (!existingIds.add(friendship.getId())) {
                throw new IllegalArgumentException("Duplicate friendship id found: " + friendship.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<FriendshipEntity> entities = friendshipMapper.toJpaEntityList(friendships);
            return friendshipMapper.toDomainEntityList(friendshipJpaRepository.saveAll(entities));
        }

        Map<Long, FriendshipEntity> existingEntities = friendshipJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(FriendshipEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Friendship", "ids", missingIds);
        }

        List<FriendshipEntity> entities = new ArrayList<>(friendships.size());
        for (Friendship friendship : friendships) {
            if (friendship.getId() == null) {
                entities.add(friendshipMapper.toJpaEntity(friendship));
                continue;
            }
            FriendshipEntity existing = existingEntities.get(friendship.getId());
            friendshipMapper.updateJpaEntity(friendship, existing);
            entities.add(existing);
        }
        return friendshipMapper.toDomainEntityList(friendshipJpaRepository.saveAll(entities));
    }
}
