package com.mayanshe.nosocomiumonline.shared.cqrs;

/**
 * 查询总线接口。
 * 负责分发查询到对应的处理器。
 */
public interface QueryBus {
    /**
     * 执行查询。
     *
     * @param query 查询对象
     * @param <Q>   查询类型
     * @param <R>   返回值类型
     * @return 查询结果
     */
    <Q extends Query, R> R ask(Q query);
}
