package com.ddd.platform.infrastructure.repository;

import com.ddd.platform.domain.role.entity.Role;
import com.ddd.platform.domain.role.repository.RoleRepository;
import com.ddd.platform.infrastructure.mapper.RoleMapper;
import com.ddd.platform.infrastructure.po.RolePO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public List<Role> findRolesByUserId(Long userId) {
        List<RolePO> rolePOs = roleMapper.selectRolesByUserId(userId);
        return rolePOs.stream().map(this::convertToDomain).collect(Collectors.toList());
    }

    private Role convertToDomain(RolePO po) {
        Role role = new Role();
        role.setId(po.getId());
        role.setRoleCode(po.getRoleCode());
        role.setRoleName(po.getRoleName());
        return role;
    }
}