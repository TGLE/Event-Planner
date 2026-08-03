package com.tgle.planner.user.infrastructure.persistence;

import com.tgle.planner.core.errorhandling.exception.ResourceNotFoundException;
import com.tgle.planner.user.domain.User;
import com.tgle.planner.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userMapper::toDomainEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            UserEntity entity = userMapper.toJpaEntity(user);
            return userMapper.toDomainEntity(userJpaRepository.save(entity));
        }
        UserEntity existingEntity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User"));
        userMapper.updateJpaEntity(user, existingEntity);
        return userMapper.toDomainEntity(userJpaRepository.save(existingEntity));
    }

    @Override
    public List<User> saveAll(List<User> users) {
        Set<Long> existingIds = new HashSet<>();

        for (User user : users) {
            if (user.getId() == null) {
                continue;
            }

            if (existingIds.contains(user.getId())) {
                throw new IllegalArgumentException("Duplicate user id found: " + user.getId());
            }

            existingIds.add(user.getId());
        }

        if (existingIds.isEmpty()) {
            List<UserEntity> entities = userMapper.toJpaEntityList(users);
            return userMapper.toDomainEntityList(userJpaRepository.saveAll(entities));
        }

        Map<Long, UserEntity> existingEntities = userJpaRepository.findAllById(existingIds)
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        if (existingEntities.size() < existingIds.size()) {
            Set<Long> missingIds = new HashSet<>(existingIds);
            missingIds.removeAll(existingEntities.keySet());
            throw new ResourceNotFoundException("User", "ids", missingIds);
        }

        List<UserEntity> entities = new ArrayList<>(users.size());
        for (User user : users) {
            if (user.getId() == null) {
                entities.add(userMapper.toJpaEntity(user));
                continue;
            }
            UserEntity existing = existingEntities.get(user.getId());
            userMapper.updateJpaEntity(user, existing);
            entities.add(existing);
        }
        return userMapper.toDomainEntityList(userJpaRepository.saveAll(entities));
    }
}
