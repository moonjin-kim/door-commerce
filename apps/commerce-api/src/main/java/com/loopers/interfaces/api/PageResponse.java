package com.loopers.interfaces.api;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private List<T> items;

    public PageResponse(int page, int size, long totalElements, List<T> items) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.items = items;
    }
}
