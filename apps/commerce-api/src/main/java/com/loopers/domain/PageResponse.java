package com.loopers.domain;

import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class PageResponse<T> {
    private int page;
    private int size;
    private List<T> items;

    public PageResponse() {}

    protected PageResponse(int page, int size, List<T> items) {
        this.page = page;
        this.size = size;
        this.items = items;
    }

    public static <T> PageResponse<T> of(int page, int size, List<T> items) {
        return new PageResponse<>(page, size, items);
    }

    /**
     * 페이지 정보(페이지 번호, 사이즈, 전체 개수)는 유지하고,
     * 내부 데이터(items)의 타입만 다른 타입으로 변환합니다. (예: Entity List -> DTO List)
     *
     * @param converter 각 아이템을 변환할 함수 (T -> U)
     * @return 새로운 타입의 아이템 리스트를 담은 PageResponse 객체
     */
    public <U> PageResponse<U> map(Function<T, U> converter) {
        List<U> convertedItems = this.items.stream()
                .map(converter)
                .collect(Collectors.toList());

        // 4. 기존 페이지 정보와 변환된 리스트를 사용하여 새로운 PageResponse<U> 객체를 생성하여 반환
        return new PageResponse<>(this.page, this.size, convertedItems);
    }
}
