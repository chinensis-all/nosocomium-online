package com.mayanshe.nosocomiumonline.shared.contract;

/**
 * 将 Create Command 转换为 Entity 的契约。
 *
 * @param <C> 命令的内容（可以是 DTO 或 Map）。
 * @param <E> 实体类型。
 */
public interface CreateCommandToEntity<C, E> {
    /**
     * 将创建命令转换为实体。
     *
     * @param command 创建命令。
     * @return 实体。
     */
    E toEntity(C command);
}
