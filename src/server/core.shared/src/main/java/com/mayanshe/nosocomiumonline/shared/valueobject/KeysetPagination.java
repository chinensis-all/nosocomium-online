/*
 * [ScrmStd] - 通用SCRM系统
 * Copyright (C) [2025] [张西海]
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mayanshe.nosocomiumonline.shared.valueobject;

import java.util.List;

/**
 * Keyset分页结果
 *
 * @param <Dto> DTO类型
 */
public record KeysetPagination<Dto>(
        List<Dto> items,
        String nextCursor,
        String previousCursor
) {

    @Override
    public List<Dto> items() {
        return items == null ? List.of() : List.copyOf(items);
    }

    public boolean hasNext() {
        return nextCursor != null && !nextCursor.isBlank();
    }

    public boolean hasPrevious() {
        return previousCursor != null && !previousCursor.isBlank();
    }

    public String nextCursorForRequest() {
        return hasNext() ? nextCursor : null;
    }

    public String previousCursorForRequest() {
        return hasPrevious() ? previousCursor : null;
    }

    public static <T> KeysetPagination<T> of(List<T> items, String nextCursor) {
        return new KeysetPagination<>(items, nextCursor, null);
    }

    public static <T> KeysetPagination<T> of(List<T> items, String nextCursor, String previousCursor) {
        return new KeysetPagination<>(items, nextCursor, previousCursor);
    }

    public static <T> KeysetPagination<T> empty() {
        return new KeysetPagination<>(List.of(), null, null);
    }
}