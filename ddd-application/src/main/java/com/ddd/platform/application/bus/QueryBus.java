package com.ddd.platform.application.bus;

import com.ddd.platform.application.query.Query;
import com.ddd.platform.application.query.QueryHandler;

/**
 * 查询总线接口
 */
public interface QueryBus {

    /**
     * 发送查询
     */
    <T> T send(Query<T> query);

    /**
     * 注册查询处理器
     */
    <Q extends Query<T>, T> void register(Class<Q> queryType, QueryHandler<Q, T> handler);
}