package com.mayanshe.nosocomiumonline.shared.cqrs;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 命令总线接口。
 * 负责分发命令到对应的处理器。
 */
public interface CommandBus {
    /**
     * 分发命令。
     *
     * @param command 命令对象
     * @param <C>     命令类型
     * @param <R>     返回值类型
     * @return 处理结果
     */
    <C extends Command, R> R dispatch(C command);
}
