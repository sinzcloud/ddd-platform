package com.ddd.platform.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== String 操作 ====================

    /**
     * 设置值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis set error: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 设置值并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis set error: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 设置值并设置过期时间（秒）
     */
    public void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取字符串值
     */
    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取Long值
     */
    public Long getLong(String key) {
        Object value = get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(value.toString());
    }

    /**
     * 获取Integer值
     */
    public Integer getInteger(String key) {
        Object value = get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(value.toString());
    }

    /**
     * 获取Boolean值
     */
    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value == null) return null;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.valueOf(value.toString());
    }

    /**
     * 设置值，仅当key不存在时
     */
    public Boolean setIfAbsent(String key, Object value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception e) {
            log.error("Redis setIfAbsent error: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置值，仅当key不存在时，带过期时间
     */
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis setIfAbsent error: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置值，仅当key存在时
     */
    public Boolean setIfPresent(String key, Object value) {
        try {
            return redisTemplate.opsForValue().setIfPresent(key, value);
        } catch (Exception e) {
            log.error("Redis setIfPresent error: key={}", key, e);
            return false;
        }
    }

    /**
     * 递增
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis increment error: key={}", key, e);
            return null;
        }
    }

    /**
     * 递减
     */
    public Long decrement(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Redis decrement error: key={}", key, e);
            return null;
        }
    }

    // ==================== Hash 操作 ====================

    /**
     * 设置Hash值
     */
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Redis hSet error: key={}, hashKey={}", key, hashKey, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 批量设置Hash值
     */
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("Redis hSetAll error: key={}", key, e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 获取Hash值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Redis hGet error: key={}, hashKey={}", key, hashKey, e);
            return null;
        }
    }

    /**
     * 获取整个Hash
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis hGetAll error: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除Hash字段
     */
    public Long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            log.error("Redis hDelete error: key={}", key, e);
            return 0L;
        }
    }

    /**
     * 判断Hash字段是否存在
     */
    public Boolean hHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            log.error("Redis hHasKey error: key={}, hashKey={}", key, hashKey, e);
            return false;
        }
    }

    /**
     * Hash递增
     */
    public Long hIncrement(String key, String hashKey, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception e) {
            log.error("Redis hIncrement error: key={}, hashKey={}", key, hashKey, e);
            return null;
        }
    }

    // ==================== List 操作 ====================

    /**
     * 从左推入列表
     */
    public Long lLeftPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().leftPush(key, value);
        } catch (Exception e) {
            log.error("Redis lLeftPush error: key={}", key, e);
            return null;
        }
    }

    /**
     * 从右推入列表
     */
    public Long lRightPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Redis lRightPush error: key={}", key, e);
            return null;
        }
    }

    /**
     * 从左弹出
     */
    public Object lLeftPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("Redis lLeftPop error: key={}", key, e);
            return null;
        }
    }

    /**
     * 从右弹出
     */
    public Object lRightPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("Redis lRightPop error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取列表范围
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis lRange error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取列表长度
     */
    public Long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("Redis lSize error: key={}", key, e);
            return null;
        }
    }

    // ==================== Set 操作 ====================

    /**
     * 添加元素到Set
     */
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Redis sAdd error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取Set所有元素
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis sMembers error: key={}", key, e);
            return null;
        }
    }

    /**
     * 判断元素是否在Set中
     */
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("Redis sIsMember error: key={}", key, e);
            return false;
        }
    }

    /**
     * 删除Set中的元素
     */
    public Long sRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("Redis sRemove error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取Set大小
     */
    public Long sSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("Redis sSize error: key={}", key, e);
            return null;
        }
    }

    // ==================== ZSet 操作 ====================

    /**
     * 向有序集合添加元素
     */
    public Boolean zAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            log.error("Redis zAdd error: key={}", key, e);
            return false;
        }
    }

    /**
     * 批量添加
     */
    public Long zAddAll(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        try {
            return redisTemplate.opsForZSet().add(key, tuples);
        } catch (Exception e) {
            log.error("Redis zAddAll error: key={}", key, e);
            return null;
        }
    }

    /**
     * 移除元素
     */
    public Long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            log.error("Redis zRemove error: key={}", key, e);
            return null;
        }
    }

    /**
     * 按分数范围移除
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("Redis zRemoveRangeByScore error: key={}", key, e);
            return null;
        }
    }

    /**
     * 按排名范围移除
     */
    public Long zRemoveRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().removeRange(key, start, end);
        } catch (Exception e) {
            log.error("Redis zRemoveRange error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("Redis zScore error: key={}", key, e);
            return null;
        }
    }

    /**
     * 增加分数
     */
    public Double zIncrementScore(String key, Object value, double delta) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception e) {
            log.error("Redis zIncrementScore error: key={}", key, e);
            return null;
        }
    }

    /**
     * 按分数范围统计数量
     */
    public Long zCount(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().count(key, min, max);
        } catch (Exception e) {
            log.error("Redis zCount error: key={}", key, e);
            return null;
        }
    }

    /**
     * 按分数范围获取元素
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("Redis zRangeByScore error: key={}", key, e);
            return null;
        }
    }

    /**
     * 按分数范围获取元素带分数
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
        } catch (Exception e) {
            log.error("Redis zRangeByScoreWithScores error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取排名（从小到大）
     */
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            log.error("Redis zRank error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取排名（从大到小）
     */
    public Long zReverseRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception e) {
            log.error("Redis zReverseRank error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取有序集合大小
     */
    public Long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("Redis zSize error: key={}", key, e);
            return null;
        }
    }

    // ==================== Key 操作 ====================

    /**
     * 删除key
     */
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete error: key={}", key, e);
            return false;
        }
    }

    /**
     * 批量删除
     */
    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis batch delete error: keys={}", keys, e);
            return 0L;
        }
    }

    /**
     * 检查key是否存在
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis hasKey error: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Redis expire error: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间（秒）
     */
    public Boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        try {
            return redisTemplate.getExpire(key, unit);
        } catch (Exception e) {
            log.error("Redis getExpire error: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取过期时间（秒）
     */
    public Long getExpire(String key) {
        return getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 持久化key（取消过期时间）
     */
    public Boolean persist(String key) {
        try {
            return redisTemplate.persist(key);
        } catch (Exception e) {
            log.error("Redis persist error: key={}", key, e);
            return false;
        }
    }

    /**
     * 获取所有匹配的key
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Redis keys error: pattern={}", pattern, e);
            return null;
        }
    }

    // ==================== 分布式锁辅助方法 ====================

    /**
     * 移除有序集合中指定分数范围内的元素（限流用）
     */
    public Long removeRangeByScore(String key, double min, double max) {
        return zRemoveRangeByScore(key, min, max);
    }

    /**
     * 统计有序集合中指定分数范围内的元素数量（限流用）
     */
    public Long countByScore(String key, double min, double max) {
        return zCount(key, min, max);
    }

    /**
     * 向有序集合添加元素（限流用）
     */
    public Boolean addToSortedSet(String key, Object value, double score) {
        return zAdd(key, value, score);
    }

    /**
     * 执行Lua脚本
     */
    public <T> T executeScript(RedisScript<T> script, List<String> keys, Object... args) {
        try {
            return redisTemplate.execute(script, keys, args);
        } catch (Exception e) {
            log.error("Redis executeScript error: script={}", script.getScriptAsString(), e);
            throw new RuntimeException("Redis脚本执行失败", e);
        }
    }
}