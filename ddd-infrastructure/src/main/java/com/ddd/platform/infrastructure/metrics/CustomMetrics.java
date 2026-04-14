package com.ddd.platform.infrastructure.metrics;

import com.ddd.platform.domain.metrics.MetricsPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMetrics implements MetricsPort {

    private final MeterRegistry meterRegistry;

    @Override
    public void recordUserRegister() {
        meterRegistry.counter("app.user.register.count").increment();
    }

    @Override
    public void recordUserLogin() {
        meterRegistry.counter("app.user.login.count").increment();
    }

    @Override
    public void recordLoginFailure() {
        meterRegistry.counter("app.user.login.failure").increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String name) {
        sample.stop(Timer.builder(name)
                .description("API response time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry));
    }
}