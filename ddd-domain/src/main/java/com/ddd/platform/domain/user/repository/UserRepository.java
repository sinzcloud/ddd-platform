package com.ddd.platform.domain.user.repository;

import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void deleteById(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(Long id);  // 添加这个方法

    // 聚合根操作
    Optional<UserAggregate> findAggregateById(Long id);

    UserAggregate saveAggregate(UserAggregate aggregate);
}