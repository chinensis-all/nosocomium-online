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
package com.mayanshe.nosocomiumonline.shared.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
public abstract class BaseKeysetQuery<TCursor> extends BaseQuery {
    public TCursor cursor;

    @Builder.Default
    public int limit = 20;

    @Builder.Default
    public boolean ascending = true;

    @Builder.Default
    public boolean isNext = true;

    /**
     * Converts the query object to a Map for database processing.
     *
     * @return Map representation of the query criteria.
     */
    public abstract Map<String, Object> toMap();
}