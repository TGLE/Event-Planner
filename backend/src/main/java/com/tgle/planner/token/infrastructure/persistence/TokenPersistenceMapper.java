package com.tgle.planner.token.infrastructure.persistence;

import com.tgle.planner.token.domain.Token;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TokenPersistenceMapper {

    @Mapping(target = "user.id", source = "userId")
    TokenEntity toJpaEntity(Token token);

    @Mapping(target = "userId", source = "user.id")
    Token toDomainEntity(TokenEntity tokenEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateJpaEntity(Token token, @MappingTarget TokenEntity tokenEntity);

    List<TokenEntity> toJpaEntityList(List<Token> tokens);

    List<Token> toDomainEntityList(List<TokenEntity> tokenEntities);
}
