package com.ddd.platform.infrastructure.repository;

import com.ddd.platform.domain.user.entity.User;
import com.ddd.platform.domain.user.valueobject.Email;
import com.ddd.platform.infrastructure.TestApplication;
import com.ddd.platform.infrastructure.mapper.UserMapper;
import com.ddd.platform.infrastructure.po.UserPO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("用户仓储集成测试")
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("保存用户应该成功")
    void testSaveUser() {
        Email email = new Email("test@example.com");
        User user = User.create("testuser", "123456", email);

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());

        UserPO po = userMapper.selectById(savedUser.getId());
        assertNotNull(po);
        assertEquals("testuser", po.getUsername());
    }

    @Test
    @DisplayName("根据ID查询用户应该成功")
    void testFindById() {
        var result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("admin", result.get().getUsername());
    }

    @Test
    @DisplayName("根据用户名查询用户应该成功")
    void testFindByUsername() {
        var result = userRepository.findByUsername("admin");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }
}