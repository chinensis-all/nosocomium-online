package com.mayanshe.nosocomiumonline.shared.cqrs;

/**
 * 查询处理器接口。
 *
 * @param <Q> 查询类型
 * @param <R> 返回值类型
 */
public interface QueryHandler<Q extends Query, R> {
    /**
     * 处理查询。
     *
     * @param query 查询对象
     * @return 查询结果
     */
    R handle(Q query);
}
