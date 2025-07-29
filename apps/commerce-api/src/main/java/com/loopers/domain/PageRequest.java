package com.loopers.domain;

import lombok.Getter;

import java.util.function.Function;

@Getter
public class PageRequest<T> {
    private final int page;
    private final int size;
    private final T params;

    protected PageRequest(int page, int size, T params) {
        this.page = page;
        this.size = size;
        this.params = params;
    }

    public static <T> PageRequest<T> of(int page, int size, T params) {
        return new PageRequest<>(page, size, params);
    }

    public int limit() {
        return size;
    }

    public long offset() {
        return (long) (page - 1) * size;
    }

    /**
     * 페이지 정보는 유지하고, 내부 파라미터(params)만 다른 타입으로 변환합니다.
     * @param converter params를 변환할 함수
     * @return 새로운 타입의 파라미터를 담은 PageRequest 객체
     */
    public <U> PageRequest<U> map(Function<T, U> converter) {
        return new PageRequest<>(this.page, this.size, converter.apply(this.params));
    }
}
