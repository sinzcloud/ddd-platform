package com.ddd.platform.domain.user.valueobject;

import com.ddd.platform.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserProfile extends BaseEntity {

    private Long id;
    private Long userId;
    private String nickname;
    private String avatar;
    private String bio;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static UserProfile create(Long userId, String nickname) {
        UserProfile profile = new UserProfile();
        profile.userId = userId;
        profile.nickname = nickname != null ? nickname : "用户" + userId;
        return profile;
    }

    public void updateProfile(String nickname, String avatar) {
        if (nickname != null && !nickname.trim().isEmpty()) {
            this.nickname = nickname;
        }
        if (avatar != null && !avatar.trim().isEmpty()) {
            this.avatar = avatar;
        }
    }

    public void updateBio(String bio) {
        this.bio = bio;
    }
}