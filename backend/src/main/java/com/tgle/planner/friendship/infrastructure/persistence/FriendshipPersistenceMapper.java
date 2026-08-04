package com.tgle.planner.friendship.infrastructure.persistence;

import com.tgle.planner.friendship.domain.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendshipPersistenceMapper {
    @Mapping(target = "requester.id", source = "requesterId")
    @Mapping(target = "receiver.id", source = "receiverId")
    FriendshipEntity toJpaEntity(Friendship friendship);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    Friendship toDomainEntity(FriendshipEntity friendshipEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    void updateJpaEntity(Friendship friendship, @MappingTarget FriendshipEntity friendshipEntity);

    List<FriendshipEntity> toJpaEntityList(List<Friendship> friendships);

    List<Friendship> toDomainEntityList(List<FriendshipEntity> friendshipEntities);
}
