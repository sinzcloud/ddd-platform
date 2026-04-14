package com.ddd.platform.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ddd.platform.domain.user.repository.UserProfileRepository;
import com.ddd.platform.domain.user.valueobject.UserProfile;
import com.ddd.platform.infrastructure.mapper.UserProfileMapper;
import com.ddd.platform.infrastructure.po.UserProfilePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile save(UserProfile profile) {
        UserProfilePO po = convertToPO(profile);
        if (profile.getId() == null) {
            userProfileMapper.insert(po);
            profile.setId(po.getId());
        } else {
            userProfileMapper.updateById(po);
        }
        return profile;
    }

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        LambdaQueryWrapper<UserProfilePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfilePO::getUserId, userId);
        UserProfilePO po = userProfileMapper.selectOne(wrapper);
        return Optional.ofNullable(po).map(this::convertToDomain);
    }

    @Override
    public void deleteByUserId(Long userId) {
        LambdaQueryWrapper<UserProfilePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfilePO::getUserId, userId);
        userProfileMapper.delete(wrapper);
    }

    private UserProfilePO convertToPO(UserProfile profile) {
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

    private UserProfile convertToDomain(UserProfilePO po) {
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
}