package com.mayanshe.nosocomiumonline.shared.cqrs;

/**
 * 命令处理器接口。
 *
 * @param <C> 命令类型
 * @param <R> 返回值类型 (如果不需要返回值，可使用 Void)
 */
public interface CommandHandler<C extends Command, R> {
    /**
     * 处理命令。
     *
     * @param command 命令对象
     * @return 处理结果
     */
    R handle(C command);
}
