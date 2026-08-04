package com.tgle.planner.chat.infrastructure.persistence;

import com.tgle.planner.chat.domain.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessagePersistenceMapper {

    @Mapping(target = "sender.id", source = "senderId")
    @Mapping(target = "recipient.id", source = "recipientId")
    MessageEntity toJpaEntity(Message message);

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "recipientId", source = "recipient.id")
    Message toDomainEntity(MessageEntity messageEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    void updateJpaEntity(Message message, @MappingTarget MessageEntity messageEntity);

    List<MessageEntity> toJpaEntityList(List<Message> messages);

    List<Message> toDomainEntityList(List<MessageEntity> messageEntities);
}
