package com.ddd.platform.domain.entity;

import com.ddd.platform.domain.event.DomainEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 实体基类
 * 提供领域事件收集和发布能力
 */
@Getter
public abstract class BaseEntity {

    /**
     * 领域事件列表
     */
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 添加领域事件
     */
    protected void addDomainEvent(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * 清除所有领域事件
     */
    public void clearEvents() {
        domainEvents.clear();
    }

    /**
     * 获取所有领域事件（只读）
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * 是否有领域事件
     */
    public boolean hasEvents() {
        return !domainEvents.isEmpty();
    }
}