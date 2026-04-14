package com.ddd.platform.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ddd.platform.domain.event.DomainEvent;
import com.ddd.platform.domain.event.DomainEventPublisher;
import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.repository.UserRepository;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.domain.user.valueobject.UserProfile;
import com.ddd.platform.infrastructure.mapper.UserMapper;
import com.ddd.platform.infrastructure.mapper.UserProfileMapper;
import com.ddd.platform.infrastructure.po.UserPO;
import com.ddd.platform.infrastructure.po.UserProfilePO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final DomainEventPublisher eventPublisher;  // 注入事件发布器

    @Override
    public User save(User user) {
        UserPO po = convertToPO(user);
        if (user.getId() == null) {
            userMapper.insert(po);
            user.setId(po.getId());
            // 更新事件中的聚合根ID
            user.setIdWithEvent(po.getId());
        } else {
            userMapper.updateById(po);
        }

        // 发布领域事件
        publishEvents(user);
        return user;
    }

    /**
     * 发布实体中的领域事件
     */
    private void publishEvents(User user) {
        if (user == null || !user.hasEvents()) {
            log.debug("没有需要发布的事件");
            return;
        }

        log.info("准备发布 {} 个领域事件", user.getDomainEvents().size());

        // 事务提交后再发布事件
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        log.info("事务已提交，开始发布领域事件");
                        for (DomainEvent event : user.getDomainEvents()) {
                            log.info("发布领域事件: {}", event.getClass().getSimpleName());
                            eventPublisher.publish(event);
                        }
                        user.clearEvents();
                        log.info("领域事件发布完成");
                    }

                    @Override
                    public void afterCompletion(int status) {
                        log.debug("事务完成，状态: {}", status);
                    }
                }
        );
    }

    @Override
    public Optional<User> findById(Long id) {
        UserPO po = userMapper.selectById(id);
        return Optional.ofNullable(po).map(this::convertToDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        UserPO po = userMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::convertToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmail, email);
        UserPO po = userMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::convertToDomain);
    }

    @Override
    public List<User> findAll() {
        List<UserPO> pos = userMapper.selectList(null);
        return pos.stream().map(this::convertToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getEmail, email);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsById(Long id) {
        return userMapper.selectById(id) != null;
    }

    @Override
    public Optional<UserAggregate> findAggregateById(Long id) {
        Optional<User> userOpt = findById(id);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        Optional<UserProfile> profileOpt = findProfileByUserId(id);
        UserProfile profile = profileOpt.orElse(null);

        return Optional.of(new UserAggregate(user, profile));
    }

    @Override
    public UserAggregate saveAggregate(UserAggregate aggregate) {
        User user = save(aggregate.getUser());
        if (aggregate.getProfile() != null) {
            aggregate.getProfile().setUserId(user.getId());
            saveProfile(aggregate.getProfile());
        }
        return aggregate;
    }

    private Optional<UserProfile> findProfileByUserId(Long userId) {
        LambdaQueryWrapper<UserProfilePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfilePO::getUserId, userId);
        UserProfilePO po = userProfileMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::convertProfileToDomain);
    }

    private void saveProfile(UserProfile profile) {
        UserProfilePO po = convertProfileToPO(profile);
        if (profile.getId() == null) {
            userProfileMapper.insert(po);
            profile.setId(po.getId());
        } else {
            userProfileMapper.updateById(po);
        }
    }

    private UserPO convertToPO(User user) {
        UserPO po = new UserPO();
        po.setId(user.getId());
        po.setUsername(user.getUsername());
        po.setPassword(user.getPassword());
        // 将 Email 值对象转换为字符串
        po.setEmail(user.getEmail() != null ? user.getEmail().getValue() : null);
        po.setPhone(user.getPhone());
        po.setStatus(user.getStatus());
        po.setCreateTime(user.getCreateTime());
        po.setUpdateTime(user.getUpdateTime());
        return po;
    }

    private User convertToDomain(UserPO po) {
        User user = new User();
        user.setId(po.getId());
        user.setUsername(po.getUsername());
        user.setPassword(po.getPassword());
        // 将字符串转换为 Email 值对象
        user.setEmail(po.getEmail() != null ? new Email(po.getEmail()) : null);
        user.setPhone(po.getPhone());
        user.setStatus(po.getStatus());
        user.setCreateTime(po.getCreateTime());
        user.setUpdateTime(po.getUpdateTime());
        return user;
    }

    private UserProfilePO convertProfileToPO(UserProfile profile) {
        UserProfilePO po = new UserProfilePO();
        po.setId(profile.getId());
        po.setUserId(profile.getUserId());
        po.setNickname(profile.getNickname());
        po.setAvatar(profile.getAvatar());
        po.setBio(profile.getBio());
        po.setCreateTime(profile.getCreateTime());
        po.setUpdateTime(profile.getUpdateTime());
        return po;
    }

    private UserProfile convertProfileToDomain(UserProfilePO po) {
        UserProfile profile = new UserProfile();
        profile.setId(po.getId());
        profile.setUserId(po.getUserId());
        profile.setNickname(po.getNickname());
        profile.setAvatar(po.getAvatar());
        profile.setBio(po.getBio());
        profile.setCreateTime(po.getCreateTime());
        profile.setUpdateTime(po.getUpdateTime());
        return profile;
    }

    /**
     * 批量插入用户（使用 MyBatis Plus 批量方法）
     */
    public void batchInsert(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }

        List<UserPO> pos = users.stream()
                .map(this::convertToPO)
                .collect(Collectors.toList());

        // 分批插入，每批500条
        int batchSize = 500;
        int total = pos.size();

        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            List<UserPO> batch = pos.subList(i, end);
            userMapper.insertBatch(batch);
        }
    }
}