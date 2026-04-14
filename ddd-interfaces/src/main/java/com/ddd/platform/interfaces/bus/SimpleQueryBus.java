package com.ddd.platform.interfaces.bus;

import com.ddd.platform.application.bus.QueryBus;
import com.ddd.platform.application.query.Query;
import com.ddd.platform.application.query.QueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SimpleQueryBus implements QueryBus {

    private final Map<Class<?>, QueryHandler<?, ?>> handlers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T send(Query<T> query) {
        QueryHandler<Query<T>, T> handler = (QueryHandler<Query<T>, T>) handlers.get(query.getClass());
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for query: " + query.getClass().getName());
        }

        log.debug("执行查询: {}", query.getClass().getSimpleName());
        long startTime = System.currentTimeMillis();

        try {
            T result = handler.handle(query);
            long costTime = System.currentTimeMillis() - startTime;
            log.debug("查询执行完成: {}, 耗时: {}ms", query.getClass().getSimpleName(), costTime);
            return result;
        } catch (Exception e) {
            log.error("查询执行失败: {}", query.getClass().getSimpleName(), e);
            throw e;
        }
    }

    @Override
    public <Q extends Query<T>, T> void register(Class<Q> queryType, QueryHandler<Q, T> handler) {
        handlers.put(queryType, handler);
        log.info("注册查询处理器: {} -> {}", queryType.getSimpleName(), handler.getClass().getSimpleName());
    }
}