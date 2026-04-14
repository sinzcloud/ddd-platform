package com.ddd.platform.application.query;

/**
 * 查询处理器接口
 */
@FunctionalInterface
public interface QueryHandler<Q extends Query<T>, T> {
    T handle(Q query);
}