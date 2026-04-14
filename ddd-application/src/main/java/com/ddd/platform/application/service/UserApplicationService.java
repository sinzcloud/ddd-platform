package com.ddd.platform.application.service;

import com.ddd.platform.application.command.RegisterCommand;
import com.ddd.platform.application.dto.UserDTO;
import com.ddd.platform.common.enums.ErrorCode;
import com.ddd.platform.common.exception.BizException;
import com.ddd.platform.domain.metrics.MetricsPort;
import com.ddd.platform.domain.user.aggregate.UserAggregate;
import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.repository.UserRepository;
import com.ddd.platform.domain.user.service.UserDomainService;
import com.ddd.platform.domain.user.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MetricsPort metricsPort;  // 通过接口注入

    @Transactional
    public UserDTO registerUser(RegisterCommand command) {
        log.info("注册用户: username={}", command.getUsername());

        // 加密密码
        String encodedPassword = passwordEncoder.encode(command.getPassword());

        // 调用领域服务创建用户
        UserAggregate aggregate = userDomainService.createUser(
                command.getUsername(),
                encodedPassword,
                command.getEmail(),
                command.getNickname() != null ? command.getNickname() : command.getUsername()
        );

        // 记录注册监控指标
        metricsPort.recordUserRegister();

        log.info("用户注册成功: userId={}", aggregate.getUserId());
        return convertToDTO(aggregate);
    }

    @Transactional
    public void activateUser(Long userId) {
        log.info("激活用户: userId={}", userId);
        userDomainService.activateUser(new UserId(userId));
        log.info("用户激活成功: userId={}", userId);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        log.info("禁用用户: userId={}", userId);
        userDomainService.deactivateUser(new UserId(userId));
        log.info("用户禁用成功: userId={}", userId);
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));
        return convertToDTO(user);
    }

    public List<UserDTO> listAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.USER_NOT_FOUND));

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmailValue())) {
            // 更新邮箱逻辑
        }

        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }

        userRepository.save(user);
        log.info("用户更新成功: userId={}", userId);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
        log.info("用户删除成功: userId={}", userId);
    }

    private UserDTO convertToDTO(UserAggregate aggregate) {
        UserDTO dto = new UserDTO();
        dto.setId(aggregate.getUserId());
        dto.setUsername(aggregate.getUsername());
        dto.setEmail(aggregate.getEmail());
        dto.setPhone(aggregate.getUser().getPhone());
        dto.setStatus(aggregate.getUser().getStatus());
        dto.setNickname(aggregate.getNickname());
        dto.setCreateTime(aggregate.getUser().getCreateTime());
        return dto;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmailValue());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        return dto;
    }
}