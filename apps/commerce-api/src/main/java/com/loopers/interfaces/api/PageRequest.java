package com.loopers.interfaces.api;

import lombok.Builder;
import lombok.Getter;

import java.awt.print.Pageable;

import static java.lang.Math.min;

@Getter
public class PageRequest {
    private static final int MAX_PAGE = 999;
    private static final int MAX_SIZE = 2000;

    private int page = 1;
    private int size = 20;

    public void setPage(Integer page) {
        this.page = page <= 0 ? 1 : min(page, MAX_PAGE);
    }

    public long getOffset() {
        return (long) (page - 1) * min(size, MAX_SIZE);
    }

}
