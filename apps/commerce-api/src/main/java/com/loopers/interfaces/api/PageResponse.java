package com.loopers.interfaces.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PageResponse<T> {
    private int pageNum;
    private int size;
    private long totalCount;
    private List<T> items;

    public PageResponse(int limit, long offset, long totalCount, List<T> items) {
        this.pageNum = (int) offset / limit;
        this.size = limit;
        this.totalCount = totalCount;
        this.items = items;
    }

    public static <T> PageResponse<T> of(int limit, long offset, long totalCount, List<T> items) {
        return new PageResponse<>(limit, offset, totalCount, items);
    }
}
