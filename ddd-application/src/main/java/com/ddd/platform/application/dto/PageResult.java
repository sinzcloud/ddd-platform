package com.ddd.platform.application.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> records;

    public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRecords(records);
        return result;
    }
}