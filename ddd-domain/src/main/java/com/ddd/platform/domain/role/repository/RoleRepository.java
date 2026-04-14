package com.ddd.platform.domain.role.repository;

import com.ddd.platform.domain.role.entity.Role;
import java.util.List;

public interface RoleRepository {
    List<Role> findRolesByUserId(Long userId);
}