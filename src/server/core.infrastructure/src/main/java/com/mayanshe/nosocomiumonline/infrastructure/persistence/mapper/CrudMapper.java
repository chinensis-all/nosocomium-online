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
package com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Mapper
public interface CrudMapper<E> {
    long insert(E entity);

    long update(E entity);

    long deleteById(Long id);

    long softDeleteById(Long id);

    E findById(Long id);

    List<E> search(Map<String, Object> criteria, int limit, int offset);

    long count(Map<String, Object> criteria);

    List<E> keysetSearch(Map<String, Object> criteria, Boolean isAsc, Boolean isNext, Long id, int limit);

    Type entityType();
}
