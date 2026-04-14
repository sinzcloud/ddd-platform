package com.ddd.platform.infrastructure.filter;

import com.ddd.platform.infrastructure.metrics.CustomMetrics;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MetricsFilter extends OncePerRequestFilter {

    private final CustomMetrics customMetrics;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Timer.Sample sample = customMetrics.startTimer();

        try {
            filterChain.doFilter(request, response);
        } finally {
            String path = request.getRequestURI();
            String method = request.getMethod();
            customMetrics.stopTimer(sample, "http." + method + "." + path);
        }
    }
}