package com.ddd.platform.infrastructure.repository;

import com.ddd.platform.infrastructure.TestApplication;
import com.ddd.platform.infrastructure.mapper.UserMapper;
import com.ddd.platform.infrastructure.po.UserPO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testBatchInsert() {
        List<UserPO> list = new ArrayList<>();

        UserPO po1 = new UserPO();
        po1.setUsername("batch_test_1");
        po1.setPassword("123456");
        po1.setEmail("batch1@test.com");

        UserPO po2 = new UserPO();
        po2.setUsername("batch_test_2");
        po2.setPassword("123456");
        po2.setEmail("batch2@test.com");

        list.add(po1);
        list.add(po2);

        int result = userMapper.insertBatch(list);
        System.out.println("插入数量: " + result);
    }
}