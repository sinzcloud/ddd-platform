package com.ddd.platform.interfaces.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Schema(description = "分页响应")
public class PageResponse<T> {

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize;

    @Schema(description = "总页数", example = "10")
    private Integer totalPages;

    @Schema(description = "数据列表")
    private List<T> records;

    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    public PageResponse() {
        this.records = Collections.emptyList();
        this.total = 0L;
        this.pageNum = 1;
        this.pageSize = 10;
        this.totalPages = 0;
        this.hasPrevious = false;
        this.hasNext = false;
    }

    public PageResponse(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < totalPages;
    }

    public static <T> PageResponse<T> of(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        return new PageResponse<>(total, pageNum, pageSize, records);
    }

    public static <T> PageResponse<T> empty() {
        return new PageResponse<>();
    }
}