package com.tgle.planner.user.infrastructure.persistence;

import com.tgle.planner.user.domain.TokenCooldown;
import com.tgle.planner.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

    @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
    public interface UserPersistenceMapper {

        UserEntity toJpaEntity(User user);

        User toDomainEntity(UserEntity userEntity);

        @Mapping(target = "id", ignore = true)
        void updateJpaEntity(User user, @MappingTarget UserEntity userEntity);

        List<UserEntity> toJpaEntityList(List<User> users);

        List<User> toDomainEntityList(List<UserEntity> usersEntities);

        TokenCooldownEmbeddable toJpaEntity(TokenCooldown tokenCooldown);

        TokenCooldown toDomainEntity(TokenCooldownEmbeddable tokenCooldownEmbeddable);
    }
