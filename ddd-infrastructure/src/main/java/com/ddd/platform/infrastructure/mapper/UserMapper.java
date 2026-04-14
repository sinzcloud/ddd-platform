package com.ddd.platform.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ddd.platform.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<UserPO> list);

    /**
     * 批量插入（忽略重复）
     */
    int insertBatchIgnore(@Param("list") List<UserPO> list);

    /**
     * 批量插入（重复更新）
     */
    int insertBatchUpdate(@Param("list") List<UserPO> list);

    /**
     * 批量更新
     */
    int updateBatch(@Param("list") List<UserPO> list);

    /**
     * 批量删除
     */
    int deleteBatch(@Param("list") List<Long> ids);
}