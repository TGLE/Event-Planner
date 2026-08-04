package com.tgle.planner.chat.infrastructure.persistence;

import com.tgle.planner.chat.domain.Message;
import com.tgle.planner.chat.domain.MessageRepository;
import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageJpaRepository messageJpaRepository;
    private final MessagePersistenceMapper messageMapper;

    @Override
    public Message save(Message message) {
        if (message.getId() == null) {
            MessageEntity entity = messageMapper.toJpaEntity(message);
            return messageMapper.toDomainEntity(messageJpaRepository.save(entity));
        }
        MessageEntity existingEntity = messageJpaRepository.findById(message.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Message"));
        messageMapper.updateJpaEntity(message, existingEntity);
        return messageMapper.toDomainEntity(existingEntity);
    }

    @Override
    public List<Message> saveAll(List<Message> messages) {
        Set<Long> existingIds = new HashSet<>();

        for (Message message : messages) {
            if (message.getId() == null) {
                continue;
            }

            if (!existingIds.add(message.getId())) {
                throw new IllegalArgumentException("Duplicate message id found: " + message.getId());
            }
        }

        if (existingIds.isEmpty()) {
            List<MessageEntity> entities = messageMapper.toJpaEntityList(messages);
            return messageMapper.toDomainEntityList(messageJpaRepository.saveAll(entities));
        }

        Map<Long, MessageEntity> existingEntities = messageJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(MessageEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("Message", "ids", missingIds);
        }

        List<MessageEntity> entities = new ArrayList<>(messages.size());
        for (Message message : messages) {
            if (message.getId() == null) {
                entities.add(messageMapper.toJpaEntity(message));
                continue;
            }
            MessageEntity existing = existingEntities.get(message.getId());
            messageMapper.updateJpaEntity(message, existing);
            entities.add(existing);
        }
        return messageMapper.toDomainEntityList(messageJpaRepository.saveAll(entities));
    }
}
