package com.ddd.platform.domain.user.aggregate;

import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.valueobject.UserProfile;
import lombok.Data;

@Data
public class UserAggregate {

    private User user;
    private UserProfile profile;

    public UserAggregate(User user, UserProfile profile) {
        this.user = user;
        this.profile = profile;
    }

    public void activate() {
        this.user.activate();
    }

    public void deactivate() {
        this.user.deactivate();
    }

    public void updateProfile(String nickname, String avatar) {
        if (this.profile != null) {
            this.profile.updateProfile(nickname, avatar);
        } else if (nickname != null || avatar != null) {
            UserProfile newProfile = UserProfile.create(this.user.getId(), nickname);
            newProfile.setAvatar(avatar);
            this.profile = newProfile;
        }
    }

    public boolean isActive() {
        return this.user.isActive();
    }

    public String getNickname() {
        return this.profile != null ? this.profile.getNickname() : this.user.getUsername();
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public String getEmail() {
        return this.user.getEmail() != null ? this.user.getEmail().getValue() : null;
    }
}