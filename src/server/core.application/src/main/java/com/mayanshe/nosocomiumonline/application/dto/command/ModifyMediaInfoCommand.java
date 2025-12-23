package com.mayanshe.nosocomiumonline.application.dto.command;

import com.mayanshe.nosocomiumonline.shared.cqrs.Command;

/**
 * 修改媒体信息命令。
 *
 * @author zhangxihai
 */
public record ModifyMediaInfoCommand(
        Long id,
        String title,
        String description
) implements Command {
    public void validate() {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("无效的媒体ID");
        }
    }

    @Override
    public String title() {
        return title != null ? title : "";
    }

    @Override
    public String description() {
        return description != null ? description : "";
    }
}
