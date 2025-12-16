package com.mayanshe.nosocomiumonline.shared.valueobject;

import java.util.List;
import java.util.Objects;

/**
 * 分页结果
 *
 * @param <Dto> DTO类型
 */
public record Pagination<Dto>(long page, long pageSize, long total, List<Dto> items) {
    @Override
    public long page() {
        return Math.max(page, 1);
    }

    @Override
    public long pageSize() {
        return Math.max(pageSize, 1);
    }

    @Override
    public List<Dto> items() {
        return items == null ? List.of() : items;
    }

    public long totalPages() {
        long size = pageSize();
        if (size == 0) return 0;
        return (total + size - 1) / size;
    }

    public boolean hasPrev() {
        return page() > 1;
    }

    public boolean hasNext() {
        return page() < totalPages();
    }

    public long prevPage() {
        return hasPrev() ? page() - 1 : page();
    }

    public long nextPage() {
        return hasNext() ? page() + 1 : page();
    }

    public static <T> Pagination<T> of(long page, long pageSize, long total, List<T> items) {
        return new Pagination<>(page, pageSize, total, items);
    }

    public static <T> Pagination<T> of(long page, long total, List<T> items) {
        return of(page, 20L, total, items);
    }

    public static <T> Pagination<T> empty() {
        return new Pagination<>(1L, 20L, 0L, List.of());
    }

    public static <T> Pagination<T> empty(long defaultPageSize) {
        return new Pagination<>(1L, defaultPageSize, 0L, List.of());
    }
}
