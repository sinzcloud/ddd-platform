package com.ddd.platform;

import com.ddd.platform.application.command.RegisterCommand;
import com.ddd.platform.application.service.UserApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class PerformanceTest {

    @Autowired
    private UserApplicationService userService;

    @Test
    @DisplayName("并发注册用户性能测试")
    void testConcurrentRegister() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    RegisterCommand command = new RegisterCommand();
                    command.setUsername("perf_test_" + index);
                    command.setPassword("123456");
                    command.setEmail("perf_" + index + "@test.com");

                    userService.registerUser(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        System.out.println("========== 性能测试结果 ==========");
        System.out.println("总请求数: " + threadCount);
        System.out.println("成功数: " + successCount.get());
        System.out.println("失败数: " + failCount.get());
        System.out.println("总耗时: " + (endTime - startTime) + "ms");
        System.out.println("QPS: " + (threadCount * 1000.0 / (endTime - startTime)));
    }
}