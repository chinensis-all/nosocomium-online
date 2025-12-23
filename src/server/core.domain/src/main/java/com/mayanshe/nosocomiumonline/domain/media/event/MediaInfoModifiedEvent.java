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
package com.mayanshe.nosocomiumonline.domain.media.event;

import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * MediaInfoModifiedEvent: 媒体信息修改领域事件
 */
@Getter
@ToString
@RequiredArgsConstructor
public class MediaInfoModifiedEvent implements DomainEvent {
    private final Long id;

    private final String md5;

    private final String title;

    private final String description;

    private final LocalDateTime occurredAt = LocalDateTime.now();

    @Override
    public LocalDateTime occurredAt() {
        return occurredAt;
    }
}
