package com.mayanshe.nosocomiumonline.application.dto.command;

import com.mayanshe.nosocomiumonline.shared.cqrs.Command;

/**
 * DeleteMediaCommand: 删除媒体命令。
 *
 * @param id
 */
public record DeleteMediaCommand(Long id) implements Command {
    public void validate() {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的媒体ID");
        }
    }
}
