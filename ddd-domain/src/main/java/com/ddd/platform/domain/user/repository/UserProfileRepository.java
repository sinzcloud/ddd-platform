package com.ddd.platform.domain.user.repository;

import com.ddd.platform.domain.user.valueobject.UserProfile;
import java.util.Optional;

public interface UserProfileRepository {

    UserProfile save(UserProfile profile);

    Optional<UserProfile> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}